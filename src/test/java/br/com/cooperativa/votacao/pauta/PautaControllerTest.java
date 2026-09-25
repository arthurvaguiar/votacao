package br.com.cooperativa.votacao.pauta;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PautaService service;

    @Test
    void deveCriarPautaERetornar201() throws Exception {
        var pauta = new Pauta("Reforma", "Aprovar reforma");
        ReflectionTestUtils.setField(pauta, "id", 1L);
        when(service.criar("Reforma", "Aprovar reforma")).thenReturn(pauta);

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": "Reforma", "descricao": "Aprovar reforma"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/pautas/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Reforma"));
    }

    @Test
    void deveRetornar400QuandoTituloVazio() throws Exception {
        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": ""}
                                """))
                .andExpect(jsonPath("$.erros[0].campo").value("titulo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoPautaNaoExiste() throws Exception {
        when(service.buscar(99L)).thenThrow(new PautaNaoEncontradaException(99L));

        mockMvc.perform(get("/api/v1/pautas/99"))
                .andExpect(status().isNotFound());
    }
}