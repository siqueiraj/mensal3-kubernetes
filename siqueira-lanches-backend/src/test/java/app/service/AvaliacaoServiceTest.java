package app.service;

import app.entity.Avaliacao;
import app.repository.AvaliacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvaliacaoServiceTest {

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodasAvaliacoes() {
        List<Avaliacao> avaliacoes = Arrays.asList(new Avaliacao(), new Avaliacao());
        when(avaliacaoRepository.findAll()).thenReturn(avaliacoes);

        List<Avaliacao> resultado = avaliacaoService.listAll();

        assertEquals(2, resultado.size());
        verify(avaliacaoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarAvaliacaoPorId() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        Optional<Avaliacao> resultado = avaliacaoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(avaliacaoRepository, times(1)).findById(1L);
    }

    @Test
    void deveSalvarAvaliacao() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setComentario("Ótimo!");
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        Avaliacao resultado = avaliacaoService.save(avaliacao);

        assertNotNull(resultado);
        assertEquals("Ótimo!", resultado.getComentario());
        verify(avaliacaoRepository, times(1)).save(avaliacao);
    }

    @Test
    void deveDeletarAvaliacao() {
        Long id = 1L;
        doNothing().when(avaliacaoRepository).deleteById(id);

        avaliacaoService.delete(id);

        verify(avaliacaoRepository, times(1)).deleteById(id);
    }
}
