package br.com.cooperativa.votacao.voto;


import br.com.cooperativa.votacao.sessao.SessaoEncerradaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    private static final String URL = "/api/v1/pautas/1/votos";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotoService service;

    @Test
    void deveRetornar400QuandoVotoInvalido() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"associadoId": "a1", "voto": "Talvez"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Talvez")));
    }

    @Test
    void deveRetornar400ComErroDeCampo() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"voto": "Sim"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("associadoId"));
    }

    @Test
    void deveRetornar409QuandoVotoDuplicado() throws Exception {
        when(service.registrar(1L, "a1", OpcaoVoto.SIM)).thenThrow(new VotoDuplicadoException(1L, "a1"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"associadoId": "a1", "voto": "Sim"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveRetornar422QuandoSessaoEncerrada() throws Exception {
        when(service.registrar(1L, "a1", OpcaoVoto.SIM)).thenThrow(new SessaoEncerradaException(1L));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"associadoId": "a1", "voto": "Sim"}
                                """))
                .andExpect(status().isUnprocessableContent());
    }
}