package br.com.cooperativa.votacao.tela.modelo;
import java.util.List;

public record TelaSelecao(String tipo, String titulo, List<ItemSelecao> itens) implements Tela {

    public TelaSelecao(String titulo, List<ItemSelecao> itens) {
        this("SELECAO", titulo, itens);
    }
}