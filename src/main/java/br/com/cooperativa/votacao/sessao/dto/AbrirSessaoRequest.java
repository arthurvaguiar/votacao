package br.com.cooperativa.votacao.sessao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(@Positive @Max(1440) Integer duracaoEmMinutos) {
}