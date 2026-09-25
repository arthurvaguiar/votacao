package br.com.cooperativa.votacao.voto.dto;

import br.com.cooperativa.votacao.voto.OpcaoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarVotoRequest(
        @NotBlank(message = "é obrigatório")
        @Size(max = 50, message = "deve ter no máximo 50 caracteres") String associadoId,
        @NotNull(message = "é obrigatório (Sim ou Não)") OpcaoVoto voto) {
}