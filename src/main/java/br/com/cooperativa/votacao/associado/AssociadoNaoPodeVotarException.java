package br.com.cooperativa.votacao.associado;

import br.com.cooperativa.votacao.comum.excecao.RegraNegocioException;

public class AssociadoNaoPodeVotarException extends RegraNegocioException {

    public AssociadoNaoPodeVotarException() {
        super("Associado não está habilitado para votar");
    }
}