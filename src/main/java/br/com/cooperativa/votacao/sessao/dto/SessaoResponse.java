package br.com.cooperativa.votacao.sessao.dto;

import br.com.cooperativa.votacao.sessao.Sessao;
import java.time.Instant;

public record SessaoResponse(Long id, Long pautaId, Instant abertura, Instant fechamento) {

    public static SessaoResponse de(Sessao sessao) {
        return new SessaoResponse(sessao.getId(), sessao.getPauta().getId(),
                sessao.getAbertura(), sessao.getFechamento());
    }
}