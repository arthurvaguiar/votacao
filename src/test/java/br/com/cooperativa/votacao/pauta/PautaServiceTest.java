package br.com.cooperativa.votacao.pauta;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository repository;

    @InjectMocks
    private PautaService service;

    @Test
    void deveCriarPauta() {
        when(repository.save(any(Pauta.class))).thenAnswer(inv -> inv.getArgument(0));

        var pauta = service.criar("Reforma", "Aprovar reforma da sede");

        assertThat(pauta.getTitulo()).isEqualTo("Reforma");
        assertThat(pauta.getCriadaEm()).isNotNull();
        verify(repository).save(any(Pauta.class));
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }
}