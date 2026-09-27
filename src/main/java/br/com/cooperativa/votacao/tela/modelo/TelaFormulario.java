package br.com.cooperativa.votacao.tela.modelo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TelaFormulario(String tipo, String titulo, List<Campo> itens,
                             Botao botaoOk, Botao botaoCancelar) implements Tela {

    public TelaFormulario(String titulo, List<Campo> itens, Botao botaoOk, Botao botaoCancelar) {
        this("FORMULARIO", titulo, itens, botaoOk, botaoCancelar);
    }
}