package br.com.cooperativa.votacao.tela.modelo;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Botao(String texto, String url, Map<String, Object> body) {

    public Botao(String texto, String url) {
        this(texto, url, null);
    }
}