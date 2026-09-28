package br.com.cooperativa.votacao.associado;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

/**
 * Simula o serviço user-info (fora do ar): CPF com dígitos inválidos é rejeitado
 * e, para CPF válido, o resultado é sorteado como no serviço original.
 */
@Component
@ConditionalOnProperty(prefix = "app.user-info", name = "modo", havingValue = "fake", matchIfMissing = true)
public class ValidadorAssociadoFake implements ValidadorAssociado {

    private static final Logger log = LoggerFactory.getLogger(ValidadorAssociadoFake.class);

    private final double chanceApto;
    private final DoubleSupplier sorteio;

    @Autowired
    public ValidadorAssociadoFake(UserInfoProperties properties) {
        this(properties.chanceApto(), () -> ThreadLocalRandom.current().nextDouble());
    }

    ValidadorAssociadoFake(double chanceApto, DoubleSupplier sorteio) {
        this.chanceApto = chanceApto;
        this.sorteio = sorteio;
    }

    @Override
    public void validarPodeVotar(String cpf) {
        var numeros = cpf.replaceAll("\\D", "");

        if (!Cpf.valido(numeros)) {
            throw new CpfInvalidoException();
        }
        if (sorteio.getAsDouble() >= chanceApto) {
            log.info("[fake] Associado não habilitado para votar: cpf={}", Cpf.mascarar(numeros));
            throw new AssociadoNaoPodeVotarException();
        }
    }
}