package br.com.cooperativa.votacao.voto.dto;
import br.com.cooperativa.votacao.voto.OpcaoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarVotoRequest(
        @NotBlank @Size(max = 50) String associadoId,
        @NotNull OpcaoVoto voto) {
}