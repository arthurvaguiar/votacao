package br.com.cooperativa.votacao.sessao;


import br.com.cooperativa.votacao.pauta.Pauta;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SessaoTest {

    private final Instant abertura = Instant.parse("2026-01-01T10:00:00Z");
    private final Sessao sessao = new Sessao(new Pauta("P", null), abertura, Duration.ofMinutes(1));

    @Test
    void deveEstarAbertaDentroDoPrazo() {
        assertThat(sessao.estaAberta(abertura.plusSeconds(30))).isTrue();
    }

    @Test
    void deveEstarFechadaNoInstanteDoFechamento() {
        assertThat(sessao.estaAberta(abertura.plusSeconds(60))).isFalse();
    }
}