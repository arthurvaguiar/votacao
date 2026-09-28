package br.com.cooperativa.votacao.associado;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CpfTest {

    @ParameterizedTest
    @ValueSource(strings = {"19839091069", "62289608068"})
    void deveAceitarCpfValido(String cpf) {
        assertThat(Cpf.valido(cpf)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"19839091068", "11111111111", "123", "abc45678901", ""})
    void deveRejeitarCpfInvalido(String cpf) {
        assertThat(Cpf.valido(cpf)).isFalse();
    }
}