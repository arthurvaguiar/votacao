package br.com.cooperativa.votacao.tela;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TelaUrls {

    private final String base;

    public TelaUrls(@Value("${app.base-url}") String baseUrl) {
        this.base = baseUrl.replaceAll("/+$", "") + "/api/v1/telas";
    }

    public String inicio() { return base; }
    public String pautas() { return base + "/pautas"; }
    public String novaPauta() { return base + "/pautas/nova"; }
    public String cadastrarPauta() { return base + "/pautas/cadastrar"; }
    public String pauta(Long id) { return base + "/pautas/" + id; }
    public String novaSessao(Long id) { return pauta(id) + "/sessao/nova"; }
    public String abrirSessao(Long id) { return pauta(id) + "/sessao/abrir"; }
    public String novoVoto(Long id) { return pauta(id) + "/voto/novo"; }
    public String opcoesVoto(Long id) { return pauta(id) + "/voto/opcoes"; }
    public String registrarVoto(Long id) { return pauta(id) + "/voto/registrar"; }
    public String resultado(Long id) { return pauta(id) + "/resultado"; }
}