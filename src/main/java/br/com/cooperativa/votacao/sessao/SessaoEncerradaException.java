package br.com.cooperativa.votacao.sessao;

import br.com.cooperativa.votacao.comum.excecao.RegraNegocioException;

public class SessaoEncerradaException extends RegraNegocioException {

    public SessaoEncerradaException(Long pautaId) {
        super("A sessão de votação da pauta " + pautaId + " está encerrada");
    }
}