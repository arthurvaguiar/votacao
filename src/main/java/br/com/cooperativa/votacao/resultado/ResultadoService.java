package br.com.cooperativa.votacao.resultado;


import br.com.cooperativa.votacao.pauta.PautaService;
import br.com.cooperativa.votacao.resultado.dto.ResultadoResponse;
import br.com.cooperativa.votacao.sessao.SessaoService;
import br.com.cooperativa.votacao.voto.OpcaoVoto;
import br.com.cooperativa.votacao.voto.VotoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class ResultadoService {

    private final PautaService pautaService;
    private final SessaoService sessaoService;
    private final VotoService votoService;
    private final Clock clock;

    public ResultadoService(PautaService pautaService, SessaoService sessaoService,
                            VotoService votoService, Clock clock) {
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.votoService = votoService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ResultadoResponse apurar(Long pautaId) {
        var pauta = pautaService.buscar(pautaId);
        var sessao = sessaoService.buscarPorPauta(pautaId);
        var contagem = votoService.contarVotos(pautaId);

        long sim = contagem.get(OpcaoVoto.SIM);
        long nao = contagem.get(OpcaoVoto.NAO);
        var status = StatusResultado.apurar(sessao.estaAberta(Instant.now(clock)), sim, nao);

        return new ResultadoResponse(pauta.getId(), pauta.getTitulo(), sim, nao,
                sim + nao, sessao.getFechamento(), status);
    }
}