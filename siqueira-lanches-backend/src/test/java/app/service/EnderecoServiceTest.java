package app.service;

import app.entity.Endereco;
import app.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnderecoServiceTest {

    @InjectMocks
    private EnderecoService enderecoService;

    @Mock
    private EnderecoRepository enderecoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodosEnderecos() {
        List<Endereco> enderecos = Arrays.asList(new Endereco(), new Endereco());
        when(enderecoRepository.findAll()).thenReturn(enderecos);

        List<Endereco> resultado = enderecoService.listAll();

        assertEquals(2, resultado.size());
        verify(enderecoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarEnderecoPorId() {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        Optional<Endereco> resultado = enderecoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(enderecoRepository, times(1)).findById(1L);
    }

    @Test
    void deveSalvarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setCidade("Foz do Iguaçu");
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        Endereco resultado = enderecoService.save(endereco);

        assertNotNull(resultado);
        assertEquals("Foz do Iguaçu", resultado.getCidade());
        verify(enderecoRepository, times(1)).save(endereco);
    }

    @Test
    void deveDeletarEndereco() {
        Long id = 1L;
        doNothing().when(enderecoRepository).deleteById(id);

        enderecoService.delete(id);

        verify(enderecoRepository, times(1)).deleteById(id);
    }
}
