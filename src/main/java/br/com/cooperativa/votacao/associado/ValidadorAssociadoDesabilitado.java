package br.com.cooperativa.votacao.associado;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.user-info", name = "enabled", havingValue = "false", matchIfMissing = true)
public class ValidadorAssociadoDesabilitado implements ValidadorAssociado {

    @Override
    public void validarPodeVotar(String cpf) {
        // Integração desligada: todos os associados podem votar
    }
}