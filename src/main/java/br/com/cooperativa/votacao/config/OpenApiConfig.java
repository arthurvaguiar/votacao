package br.com.cooperativa.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("API de Votação em Assembleias")
                .version("v1")
                .description("Pautas, sessões de votação, votos e apuração. "
                        + "Inclui a API de telas (FORMULARIO/SELECAO) consumida pelo app mobile."));
    }

    @Bean
    public GroupedOpenApi apiNegocio() {
        return GroupedOpenApi.builder().group("1-negocio").pathsToMatch("/api/v1/pautas/**").build();
    }

    @Bean
    public GroupedOpenApi apiTelas() {
        return GroupedOpenApi.builder().group("2-telas-mobile").pathsToMatch("/api/v1/telas/**").build();
    }
}