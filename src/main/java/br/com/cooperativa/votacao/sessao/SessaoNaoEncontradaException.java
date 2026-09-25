package br.com.cooperativa.votacao.sessao;

import br.com.cooperativa.votacao.comum.excecao.RecursoNaoEncontradoException;

public class SessaoNaoEncontradaException extends RecursoNaoEncontradoException {

    public SessaoNaoEncontradaException(Long pautaId) {
        super("Sessão de votação não encontrada para a pauta: " + pautaId);
    }
}