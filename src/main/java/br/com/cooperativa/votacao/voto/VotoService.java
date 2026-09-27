package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.associado.Cpf;
import br.com.cooperativa.votacao.associado.ValidadorAssociado;
import br.com.cooperativa.votacao.sessao.SessaoEncerradaException;
import br.com.cooperativa.votacao.sessao.SessaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Service
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository repository;
    private final SessaoService sessaoService;
    private final ValidadorAssociado validadorAssociado;

    private final Clock clock;

    public VotoService(VotoRepository repository, SessaoService sessaoService, ValidadorAssociado validadorAssociado, Clock clock) {
        this.repository = repository;
        this.sessaoService = sessaoService;
        this.validadorAssociado = validadorAssociado;
        this.clock = clock;
    }

    public Voto registrar(Long pautaId, String associadoId, OpcaoVoto opcao) {
        var sessao = sessaoService.buscarPorPauta(pautaId);
        var agora = Instant.now(clock);

        if (!sessao.estaAberta(agora)) {
            throw new SessaoEncerradaException(pautaId);
        }
        if (repository.existsByPautaIdAndAssociadoId(pautaId, associadoId)) {
            throw new VotoDuplicadoException(pautaId, associadoId);
        }

        validadorAssociado.validarPodeVotar(associadoId);

        try {
            var voto = repository.saveAndFlush(new Voto(pautaId, associadoId, opcao, agora));
            log.info("Voto registrado: pautaId={}, associado={}", pautaId, Cpf.mascarar(associadoId));
            return voto;
        } catch (DataIntegrityViolationException e) {
            throw new VotoDuplicadoException(pautaId, associadoId);
        }
    }

    @Transactional(readOnly = true)
    public Map<OpcaoVoto, Long> contarVotos(Long pautaId) {
        var contagem = new EnumMap<OpcaoVoto, Long>(OpcaoVoto.class);
        for (var opcao : OpcaoVoto.values()) {
            contagem.put(opcao, 0L);
        }
        repository.contarPorOpcao(pautaId)
                .forEach(c -> contagem.put(c.opcao(), c.total()));
        return contagem;
    }
}