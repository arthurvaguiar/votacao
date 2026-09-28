package br.com.cooperativa.votacao.associado;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidadorAssociadoFakeTest {

    private static final String CPF_VALIDO = "198.390.910-69";

    @Test
    void deveRejeitarCpfComDigitosInvalidos() {
        var validador = new ValidadorAssociadoFake(1.0, () -> 0.0);

        assertThatThrownBy(() -> validador.validarPodeVotar("19839091068"))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    void devePermitirQuandoSorteioAbaixoDaChance() {
        var validador = new ValidadorAssociadoFake(0.5, () -> 0.2);

        assertThatCode(() -> validador.validarPodeVotar(CPF_VALIDO)).doesNotThrowAnyException();
    }

    @Test
    void deveBloquearQuandoSorteioAcimaDaChance() {
        var validador = new ValidadorAssociadoFake(0.5, () -> 0.7);

        assertThatThrownBy(() -> validador.validarPodeVotar(CPF_VALIDO))
                .isInstanceOf(AssociadoNaoPodeVotarException.class);
    }
}