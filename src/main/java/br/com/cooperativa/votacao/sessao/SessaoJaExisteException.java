package br.com.cooperativa.votacao.sessao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SessaoJaExisteException extends RuntimeException {

    public SessaoJaExisteException(Long pautaId) {
        super("Já existe sessão de votação para a pauta: " + pautaId);
    }
}