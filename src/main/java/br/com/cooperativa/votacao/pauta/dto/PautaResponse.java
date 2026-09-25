package br.com.cooperativa.votacao.pauta.dto;

import br.com.cooperativa.votacao.pauta.Pauta;
import java.time.Instant;

public record PautaResponse(Long id, String titulo, String descricao, Instant criadaEm) {

    public static PautaResponse de(Pauta pauta) {
        return new PautaResponse(pauta.getId(), pauta.getTitulo(),
                pauta.getDescricao(), pauta.getCriadaEm());
    }
}