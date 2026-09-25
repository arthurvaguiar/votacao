package br.com.cooperativa.votacao.resultado.dto;

import br.com.cooperativa.votacao.resultado.StatusResultado;
import java.time.Instant;

public record ResultadoResponse(
        Long pautaId,
        String titulo,
        long votosSim,
        long votosNao,
        long totalVotos,
        Instant fechamentoSessao,
        StatusResultado status) {
}