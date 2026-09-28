package br.com.cooperativa.votacao;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "app.user-info.modo=desabilitado")
@ActiveProfiles("h2")
@AutoConfigureMockMvc
class FluxoVotacaoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void deveExecutarVotacaoCompletaPelaApiRest() throws Exception {
        var pauta = mockMvc.perform(post("/api/v1/pautas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"Pauta IT\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer pautaId = JsonPath.read(pauta, "$.id");
        var base = "/api/v1/pautas/" + pautaId;

        mockMvc.perform(post(base + "/sessao").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duracaoEmMinutos\": 5}"))
                .andExpect(status().isCreated());

        votar(base, "a1", "Sim");
        votar(base, "a2", "Sim");
        votar(base, "a3", "Nao");

        mockMvc.perform(post(base + "/votos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"a1\", \"voto\": \"Nao\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post(base + "/sessao"))
                .andExpect(status().isConflict());

        mockMvc.perform(get(base + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.votosSim").value(2))
                .andExpect(jsonPath("$.votosNao").value(1))
                .andExpect(jsonPath("$.totalVotos").value(3))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void deveNavegarPelasTelasComoOAppMobile() throws Exception {
        var inicio = enviar("/api/v1/telas", null);
        assertThat((String) JsonPath.read(inicio, "$.tipo")).isEqualTo("SELECAO");

        var cadastrada = enviar("/api/v1/telas/pautas/cadastrar", "{\"titulo\": \"Pauta via telas\"}");
        var formSessao = enviar(caminho(cadastrada, "$.botaoOk.url"), null);
        var sessaoAberta = enviar(caminho(formSessao, "$.botaoOk.url"), "{\"duracaoEmMinutos\": 5}");
        var formCpf = enviar(caminho(sessaoAberta, "$.botaoOk.url"), null);
        var opcoes = enviar(caminho(formCpf, "$.botaoOk.url"), "{\"associadoId\": \"19839091069\"}");

        Map<String, Object> bodySim = JsonPath.read(opcoes, "$.itens[0].body");
        var registrado = enviar(caminho(opcoes, "$.itens[0].url"), jsonMapper.writeValueAsString(bodySim));

        var resultado = enviar(caminho(registrado, "$.botaoOk.url"), null);
        assertThat(resultado).contains("Sim: 1", "Total de votos: 1");
    }

    private void votar(String base, String associado, String voto) throws Exception {
        mockMvc.perform(post(base + "/votos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"" + associado + "\", \"voto\": \"" + voto + "\"}"))
                .andExpect(status().isCreated());
    }

    /** Faz POST como o app: segue a URL recebida na tela anterior. */
    private String enviar(String caminho, String body) throws Exception {
        var requisicao = post(caminho);
        if (body != null) {
            requisicao.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return mockMvc.perform(requisicao)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private String caminho(String json, String jsonPath) {
        String url = JsonPath.read(json, jsonPath);
        return URI.create(url).getPath();
    }
}