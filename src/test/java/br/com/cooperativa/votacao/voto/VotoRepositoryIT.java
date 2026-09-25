package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.TestcontainersConfiguration;
import br.com.cooperativa.votacao.pauta.Pauta;
import br.com.cooperativa.votacao.pauta.PautaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class VotoRepositoryIT {

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Test
    void deveContarVotosPorOpcao() {
        var pauta = pautaRepository.save(new Pauta("Pauta IT", null));
        var agora = Instant.now();
        votoRepository.save(new Voto(pauta.getId(), "a1", OpcaoVoto.SIM, agora));
        votoRepository.save(new Voto(pauta.getId(), "a2", OpcaoVoto.SIM, agora));
        votoRepository.save(new Voto(pauta.getId(), "a3", OpcaoVoto.NAO, agora));

        var contagem = votoRepository.contarPorOpcao(pauta.getId());

        assertThat(contagem).containsExactlyInAnyOrder(
                new ContagemVoto(OpcaoVoto.SIM, 2L),
                new ContagemVoto(OpcaoVoto.NAO, 1L));
    }
}