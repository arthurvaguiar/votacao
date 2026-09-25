package br.com.cooperativa.votacao.sessao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(@Positive(message = "deve ser maior que zero")
                                 @Max(value = 1440, message = "deve ser no máximo 1440 (24 horas)") Integer duracaoEmMinutos) {
}