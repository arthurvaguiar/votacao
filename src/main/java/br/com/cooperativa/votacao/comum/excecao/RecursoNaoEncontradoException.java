package br.com.cooperativa.votacao.comum.excecao;

public abstract class RecursoNaoEncontradoException extends RuntimeException {
    protected RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}