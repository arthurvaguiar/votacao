package br.com.cooperativa.votacao.sessao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class SessaoEncerradaException extends RuntimeException {

    public SessaoEncerradaException(Long pautaId) {
        super("A sessão de votação da pauta " + pautaId + " está encerrada");
    }
}