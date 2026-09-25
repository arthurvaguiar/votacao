package br.com.cooperativa.votacao.pauta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarPautaRequest(
        @NotBlank(message = "é obrigatório")
        @Size(max = 150, message = "deve ter no máximo 150 caracteres") String titulo,
        @Size(max = 1000, message = "deve ter no máximo 1000 caracteres") String descricao) {
}