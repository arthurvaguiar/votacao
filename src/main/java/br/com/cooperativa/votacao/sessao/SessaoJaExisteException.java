package br.com.cooperativa.votacao.sessao;

import br.com.cooperativa.votacao.comum.excecao.ConflitoException;

public class SessaoJaExisteException extends ConflitoException {

    public SessaoJaExisteException(Long pautaId) {
        super("Já existe sessão de votação para a pauta: " + pautaId);
    }
}