package br.com.cooperativa.votacao.resultado;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatusResultadoTest {

    @ParameterizedTest
    @CsvSource({
            "true,  5, 1, EM_ANDAMENTO",
            "false, 5, 1, APROVADA",
            "false, 1, 5, REPROVADA",
            "false, 3, 3, EMPATE",
            "false, 0, 0, EMPATE"
    })
    void deveApurarStatus(boolean aberta, long sim, long nao, StatusResultado esperado) {
        assertThat(StatusResultado.apurar(aberta, sim, nao)).isEqualTo(esperado);
    }
}