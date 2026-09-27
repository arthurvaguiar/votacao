package br.com.cooperativa.votacao.tela.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IdentificacaoAssociadoRequest(
        @NotBlank(message = "é obrigatório")
        @Size(max = 50, message = "deve ter no máximo 50 caracteres") String associadoId) {
}