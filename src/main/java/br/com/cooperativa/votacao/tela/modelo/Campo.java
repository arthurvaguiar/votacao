package br.com.cooperativa.votacao.tela.modelo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Campo(String tipo, String id, String titulo, Object valor, String texto) {

    public static Campo texto(String texto) {
        return new Campo("TEXTO", null, null, null, texto);
    }

    public static Campo inputTexto(String id, String titulo, String valor) {
        return new Campo("INPUT_TEXTO", id, titulo, valor, null);
    }

    public static Campo inputNumero(String id, String titulo, Number valor) {
        return new Campo("INPUT_NUMERO", id, titulo, valor, null);
    }

    public static Campo inputData(String id, String titulo, String valor) {
        return new Campo("INPUT_DATA", id, titulo, valor, null);
    }
}