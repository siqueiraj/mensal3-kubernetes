package app.service;

import app.entity.Produto;
import app.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodosProdutos() {
        List<Produto> produtos = Arrays.asList(new Produto(), new Produto());
        when(produtoRepository.findAll()).thenReturn(produtos);

        List<Produto> resultado = produtoService.listAll();

        assertEquals(2, resultado.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarProdutoPorId() {
        Produto produto = new Produto();
        produto.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Optional<Produto> resultado = produtoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void deveSalvarProduto() {
        Produto produto = new Produto();
        produto.setNome("Hamburguer");
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = produtoService.save(produto);

        assertNotNull(resultado);
        assertEquals("Hamburguer", resultado.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveDeletarProduto() {
        Long id = 1L;
        doNothing().when(produtoRepository).deleteById(id);

        produtoService.delete(id);

        verify(produtoRepository, times(1)).deleteById(id);
    }

    @Test
    void deveBuscarProdutosPorNome() {
        List<Produto> produtos = Arrays.asList(new Produto(), new Produto());
        when(produtoRepository.findByNomeContainingIgnoreCase("lanche")).thenReturn(produtos);

        List<Produto> resultado = produtoService.findByNome("lanche");

        assertEquals(2, resultado.size());
        verify(produtoRepository, times(1)).findByNomeContainingIgnoreCase("lanche");
    }
}
