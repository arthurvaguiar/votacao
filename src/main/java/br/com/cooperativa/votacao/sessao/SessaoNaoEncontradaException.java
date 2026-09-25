package br.com.cooperativa.votacao.sessao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(Long pautaId) {
        super("Sessão de votação não encontrada para a pauta: " + pautaId);    }
}