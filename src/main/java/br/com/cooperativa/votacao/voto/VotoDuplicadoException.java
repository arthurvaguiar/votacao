package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.comum.excecao.ConflitoException;

public class VotoDuplicadoException extends ConflitoException {

    public VotoDuplicadoException(Long pautaId, String associadoId) {
        super("Associado " + associadoId + " já votou na pauta " + pautaId);
    }
}