package br.com.cooperativa.votacao.associado;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "app.user-info")
public record UserInfoProperties(boolean enabled, String url, Duration timeout) {
}