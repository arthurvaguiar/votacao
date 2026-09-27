package br.com.cooperativa.votacao.associado;

import br.com.cooperativa.votacao.comum.excecao.IntegracaoIndisponivelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class UserInfoValidadorAssociadoTest {

    private static final String URL = "http://user-info/users/19839091069";

    private MockRestServiceServer server;
    private UserInfoValidadorAssociado validador;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder().baseUrl("http://user-info");
        server = MockRestServiceServer.bindTo(builder).build();
        validador = new UserInfoValidadorAssociado(builder.build());
    }

    @Test
    void devePermitirQuandoAbleToVote() {
        server.expect(requestTo(URL))
                .andRespond(withSuccess("{\"status\":\"ABLE_TO_VOTE\"}", MediaType.APPLICATION_JSON));

        assertThatCode(() -> validador.validarPodeVotar("198.390.910-69")).doesNotThrowAnyException();
        server.verify();
    }

    @Test
    void deveBloquearQuandoUnableToVote() {
        server.expect(requestTo(URL))
                .andRespond(withSuccess("{\"status\":\"UNABLE_TO_VOTE\"}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> validador.validarPodeVotar("19839091069"))
                .isInstanceOf(AssociadoNaoPodeVotarException.class);
    }

    @Test
    void deveLancarCpfInvalidoQuando404() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> validador.validarPodeVotar("19839091069"))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    void deveLancarIndisponivelQuandoServicoFalha() {
        server.expect(requestTo(URL)).andRespond(withServerError());

        assertThatThrownBy(() -> validador.validarPodeVotar("19839091069"))
                .isInstanceOf(IntegracaoIndisponivelException.class);
    }
}