package br.com.cooperativa.votacao.sessao;


import br.com.cooperativa.votacao.pauta.Pauta;
import br.com.cooperativa.votacao.pauta.PautaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    private static final Instant AGORA = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private SessaoRepository repository;

    @Mock
    private PautaService pautaService;

    private SessaoService service;

    @BeforeEach
    void setUp() {
        service = new SessaoService(repository, pautaService, Clock.fixed(AGORA, ZoneOffset.UTC));
    }

    @Test
    void deveAbrirSessaoComDuracaoPadraoDeUmMinuto() {
        when(pautaService.buscar(1L)).thenReturn(new Pauta("P", null));
        when(repository.save(any(Sessao.class))).thenAnswer(inv -> inv.getArgument(0));

        var sessao = service.abrir(1L, null);

        assertThat(sessao.getFechamento()).isEqualTo(AGORA.plus(Duration.ofMinutes(1)));
    }

    @Test
    void deveAbrirSessaoComDuracaoInformada() {
        when(pautaService.buscar(1L)).thenReturn(new Pauta("P", null));
        when(repository.save(any(Sessao.class))).thenAnswer(inv -> inv.getArgument(0));

        var sessao = service.abrir(1L, 10);

        assertThat(sessao.getFechamento()).isEqualTo(AGORA.plus(Duration.ofMinutes(10)));
    }

    @Test
    void naoDeveAbrirSegundaSessaoParaMesmaPauta() {
        when(pautaService.buscar(1L)).thenReturn(new Pauta("P", null));
        when(repository.existsByPautaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.abrir(1L, null))
                .isInstanceOf(SessaoJaExisteException.class);
        verify(repository, never()).save(any());
    }
}