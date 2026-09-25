package br.com.cooperativa.votacao.voto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class OpcaoVotoTest {

    @ParameterizedTest
    @ValueSource(strings = {"SIM", "Sim", "sim", " sim "})
    void deveAceitarVariacoesDeSim(String valor) {
        assertThat(OpcaoVoto.de(valor)).isEqualTo(OpcaoVoto.SIM);
    }

    @ParameterizedTest
    @ValueSource(strings = {"NAO", "Não", "não", "nao"})
    void deveAceitarVariacoesDeNao(String valor) {
        assertThat(OpcaoVoto.de(valor)).isEqualTo(OpcaoVoto.NAO);
    }

    @Test
    void deveRejeitarValorInvalido() {
        assertThatThrownBy(() -> OpcaoVoto.de("Talvez"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}