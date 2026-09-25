package br.com.cooperativa.votacao.sessao;


import br.com.cooperativa.votacao.pauta.PautaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class SessaoService {

    static final Duration DURACAO_PADRAO = Duration.ofMinutes(1);

    private static final Logger log = LoggerFactory.getLogger(SessaoService.class);

    private final SessaoRepository repository;
    private final PautaService pautaService;
    private final Clock clock;

    public SessaoService(SessaoRepository repository, PautaService pautaService, Clock clock) {
        this.repository = repository;
        this.pautaService = pautaService;
        this.clock = clock;
    }

    @Transactional
    public Sessao abrir(Long pautaId, Integer duracaoEmMinutos) {
        var pauta = pautaService.buscar(pautaId);

        if (repository.existsByPautaId(pautaId)) {
            throw new SessaoJaExisteException(pautaId);
        }

        var duracao = duracaoEmMinutos == null ? DURACAO_PADRAO : Duration.ofMinutes(duracaoEmMinutos);
        var sessao = repository.save(new Sessao(pauta, Instant.now(clock), duracao));

        log.info("Sessão aberta: pautaId={}, fechamento={}", pautaId, sessao.getFechamento());
        return sessao;
    }
}