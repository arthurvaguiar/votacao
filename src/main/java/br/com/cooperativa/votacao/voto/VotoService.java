package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.sessao.SessaoEncerradaException;
import br.com.cooperativa.votacao.sessao.SessaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository repository;
    private final SessaoService sessaoService;
    private final Clock clock;

    public VotoService(VotoRepository repository, SessaoService sessaoService, Clock clock) {
        this.repository = repository;
        this.sessaoService = sessaoService;
        this.clock = clock;
    }

    @Transactional
    public Voto registrar(Long pautaId, String associadoId, OpcaoVoto opcao) {
        var sessao = sessaoService.buscarPorPauta(pautaId);
        var agora = Instant.now(clock);

        if (!sessao.estaAberta(agora)) {
            throw new SessaoEncerradaException(pautaId);
        }
        if (repository.existsByPautaIdAndAssociadoId(pautaId, associadoId)) {
            throw new VotoDuplicadoException(pautaId, associadoId);
        }

        try {
            var voto = repository.saveAndFlush(new Voto(pautaId, associadoId, opcao, agora));
            log.info("Voto registrado: pautaId={}, associadoId={}", pautaId, associadoId);
            return voto;
        } catch (DataIntegrityViolationException e) {
            // Dois votos simultâneos do mesmo associado: a constraint do banco barra o segundo
            throw new VotoDuplicadoException(pautaId, associadoId);
        }
    }
}