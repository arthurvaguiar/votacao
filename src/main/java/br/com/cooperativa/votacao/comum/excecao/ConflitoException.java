package br.com.cooperativa.votacao.comum.excecao;

public abstract class ConflitoException extends RuntimeException {
    protected ConflitoException(String mensagem) {
        super(mensagem);
    }
}