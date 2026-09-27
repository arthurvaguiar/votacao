package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.associado.AssociadoNaoPodeVotarException;
import br.com.cooperativa.votacao.associado.ValidadorAssociado;
import br.com.cooperativa.votacao.pauta.Pauta;
import br.com.cooperativa.votacao.sessao.Sessao;
import br.com.cooperativa.votacao.sessao.SessaoEncerradaException;
import br.com.cooperativa.votacao.sessao.SessaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    private static final Instant AGORA = Instant.parse("2026-01-01T10:00:30Z");
    private static final Instant ABERTURA = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private VotoRepository repository;

    @Mock
    private SessaoService sessaoService;

    @Mock
    private ValidadorAssociado validadorAssociado;

    private VotoService service;

    @BeforeEach
    void setUp() {
        service = new VotoService(repository, sessaoService, validadorAssociado, Clock.fixed(AGORA, ZoneOffset.UTC));
    }

    private Sessao sessaoComDuracao(Duration duracao) {
        return new Sessao(new Pauta("P", null), ABERTURA, duracao);
    }

    @Test
    void naoDeveRegistrarQuandoAssociadoNaoPodeVotar() {
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessaoComDuracao(Duration.ofMinutes(1)));
        doThrow(new AssociadoNaoPodeVotarException()).when(validadorAssociado).validarPodeVotar("assoc-1");

        assertThatThrownBy(() -> service.registrar(1L, "assoc-1", OpcaoVoto.SIM))
                .isInstanceOf(AssociadoNaoPodeVotarException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void naoDeveConsultarServicoExternoQuandoVotoDuplicado() {
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessaoComDuracao(Duration.ofMinutes(1)));
        when(repository.existsByPautaIdAndAssociadoId(1L, "assoc-1")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(1L, "assoc-1", OpcaoVoto.SIM))
                .isInstanceOf(VotoDuplicadoException.class);
        verifyNoInteractions(validadorAssociado);
    }

    @Test
    void deveRegistrarVotoComSessaoAberta() {
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessaoComDuracao(Duration.ofMinutes(1)));
        when(repository.saveAndFlush(any(Voto.class))).thenAnswer(inv -> inv.getArgument(0));

        var voto = service.registrar(1L, "assoc-1", OpcaoVoto.SIM);

        assertThat(voto.getOpcao()).isEqualTo(OpcaoVoto.SIM);
        assertThat(voto.getRegistradoEm()).isEqualTo(AGORA);
    }

    @Test
    void naoDeveVotarComSessaoEncerrada() {
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessaoComDuracao(Duration.ofSeconds(10)));

        assertThatThrownBy(() -> service.registrar(1L, "assoc-1", OpcaoVoto.SIM))
                .isInstanceOf(SessaoEncerradaException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void naoDeveVotarDuasVezesNaMesmaPauta() {
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessaoComDuracao(Duration.ofMinutes(1)));
        when(repository.existsByPautaIdAndAssociadoId(1L, "assoc-1")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(1L, "assoc-1", OpcaoVoto.NAO))
                .isInstanceOf(VotoDuplicadoException.class);
    }

    @Test
    void deveTratarVotoSimultaneoBarradoPeloBanco() {
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessaoComDuracao(Duration.ofMinutes(1)));
        when(repository.saveAndFlush(any(Voto.class))).thenThrow(new DataIntegrityViolationException("uk"));

        assertThatThrownBy(() -> service.registrar(1L, "assoc-1", OpcaoVoto.SIM))
                .isInstanceOf(VotoDuplicadoException.class);
    }
}