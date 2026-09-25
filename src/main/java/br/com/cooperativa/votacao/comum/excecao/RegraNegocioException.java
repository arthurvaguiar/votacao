package br.com.cooperativa.votacao.comum.excecao;

public abstract class RegraNegocioException extends RuntimeException {
    protected RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}