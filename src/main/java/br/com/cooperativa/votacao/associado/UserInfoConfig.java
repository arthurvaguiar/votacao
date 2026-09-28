package br.com.cooperativa.votacao.associado;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(prefix = "app.user-info", name = "modo", havingValue = "http")
public class UserInfoConfig {

    @Bean
    public ValidadorAssociado userInfoValidadorAssociado(UserInfoProperties properties) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.timeout());
        requestFactory.setReadTimeout(properties.timeout());

        var restClient = RestClient.builder()
                .baseUrl(properties.url())
                .requestFactory(requestFactory)
                .build();
        return new UserInfoValidadorAssociado(restClient);
    }
}