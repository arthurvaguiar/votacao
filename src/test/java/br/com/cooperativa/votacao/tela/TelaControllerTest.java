package br.com.cooperativa.votacao.tela;

import br.com.cooperativa.votacao.pauta.PautaService;
import br.com.cooperativa.votacao.resultado.ResultadoService;
import br.com.cooperativa.votacao.sessao.SessaoService;
import br.com.cooperativa.votacao.voto.OpcaoVoto;
import br.com.cooperativa.votacao.voto.VotoDuplicadoException;
import br.com.cooperativa.votacao.voto.VotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TelaController.class)
@Import({TelaService.class, TelaUrls.class})
@TestPropertySource(properties = "app.base-url=http://app.teste")
class TelaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private PautaService pautaService;
    @MockitoBean private SessaoService sessaoService;
    @MockitoBean private VotoService votoService;
    @MockitoBean private ResultadoService resultadoService;

    @Test
    void deveRetornarTelaInicialDeSelecao() throws Exception {
        mockMvc.perform(get("/api/v1/telas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("SELECAO"))
                .andExpect(jsonPath("$.itens[0].url").value("http://app.teste/api/v1/telas/pautas/nova"));
    }

    @Test
    void deveRetornarFormularioDeNovaPauta() throws Exception {
        mockMvc.perform(post("/api/v1/telas/pautas/nova"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andExpect(jsonPath("$.itens[0].tipo").value("INPUT_TEXTO"))
                .andExpect(jsonPath("$.itens[0].id").value("titulo"))
                .andExpect(jsonPath("$.botaoOk.url").value("http://app.teste/api/v1/telas/pautas/cadastrar"));
    }

    @Test
    void deveLevarAssociadoNoBodyDasOpcoesDeVoto() throws Exception {
        mockMvc.perform(post("/api/v1/telas/pautas/1/voto/opcoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"associadoId": "assoc-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("SELECAO"))
                .andExpect(jsonPath("$.itens[0].body.associadoId").value("assoc-1"))
                .andExpect(jsonPath("$.itens[0].body.voto").value("SIM"))
                .andExpect(jsonPath("$.itens[1].body.voto").value("NAO"));
    }

    @Test
    void deveRetornarTelaDeErroQuandoVotoDuplicado() throws Exception {
        when(votoService.registrar(1L, "assoc-1", OpcaoVoto.SIM))
                .thenThrow(new VotoDuplicadoException(1L, "assoc-1"));

        mockMvc.perform(post("/api/v1/telas/pautas/1/voto/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"associadoId": "assoc-1", "voto": "SIM"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andExpect(jsonPath("$.itens[0].tipo").value("TEXTO"))
                .andExpect(jsonPath("$.itens[0].texto", containsString("assoc-1")))
                .andExpect(jsonPath("$.botaoOk.url").value("http://app.teste/api/v1/telas"));
    }

    @Test
    void deveRetornarTelaDeErroQuandoTituloVazio() throws Exception {
        mockMvc.perform(post("/api/v1/telas/pautas/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andExpect(jsonPath("$.itens[0].texto", containsString("titulo")));
    }
}