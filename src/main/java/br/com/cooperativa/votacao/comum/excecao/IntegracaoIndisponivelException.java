package br.com.cooperativa.votacao.comum.excecao;

public class IntegracaoIndisponivelException extends RuntimeException {

    public IntegracaoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}