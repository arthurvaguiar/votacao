package br.com.cooperativa.votacao.pauta;

import br.com.cooperativa.votacao.comum.excecao.RecursoNaoEncontradoException;

public class PautaNaoEncontradaException extends RecursoNaoEncontradoException {

    public PautaNaoEncontradaException(Long id) {
        super("Pauta não encontrada: " + id);
    }
}