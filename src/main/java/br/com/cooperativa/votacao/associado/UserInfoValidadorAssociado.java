package br.com.cooperativa.votacao.associado;


import br.com.cooperativa.votacao.comum.excecao.IntegracaoIndisponivelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class UserInfoValidadorAssociado implements ValidadorAssociado {

    private static final Logger log = LoggerFactory.getLogger(UserInfoValidadorAssociado.class);
    private static final String PODE_VOTAR = "ABLE_TO_VOTE";

    private final RestClient restClient;

    public UserInfoValidadorAssociado(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void validarPodeVotar(String cpf) {
        var cpfNumeros = cpf.replaceAll("\\D", "");
        var resposta = consultar(cpfNumeros);

        if (resposta == null || !PODE_VOTAR.equals(resposta.status())) {
            log.info("Associado não habilitado para votar: cpf={}", Cpf.mascarar(cpfNumeros));
            throw new AssociadoNaoPodeVotarException();
        }
    }

    private UserInfoResponse consultar(String cpf) {
        try {
            return restClient.get()
                    .uri("/users/{cpf}", cpf)
                    .retrieve()
                    .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (req, res) -> {
                        throw new CpfInvalidoException();
                    })
                    .body(UserInfoResponse.class);
        } catch (RestClientException e) {
            log.error("Falha ao consultar serviço de validação de CPF: {}", e.getMessage());
            throw new IntegracaoIndisponivelException("Serviço de validação de CPF indisponível. Tente novamente.");
        }
    }

    record UserInfoResponse(String status) {
    }
}