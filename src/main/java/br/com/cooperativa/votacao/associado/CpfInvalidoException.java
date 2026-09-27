package br.com.cooperativa.votacao.associado;

import br.com.cooperativa.votacao.comum.excecao.RegraNegocioException;

public class CpfInvalidoException extends RegraNegocioException {

    public CpfInvalidoException() {
        super("CPF inválido");
    }
}