package app.service;

import app.dto.PedidoDTO;
import app.entity.ItemPedido;
import app.entity.Pedido;
import app.entity.Produto;
import app.entity.Usuario;
import app.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveSalvarPedidoComItens() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setPreco(new BigDecimal("10.00"));

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(2L);

        Pedido pedido = new Pedido();
        pedido.setItens(Arrays.asList(item));

        when(produtoService.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.save(pedido);

        assertNotNull(resultado);
        assertEquals(20.00, resultado.getValorTotal());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveCriarPedidoFromDTO() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setPreco(new BigDecimal("10.00"));

        PedidoDTO.ItemDTO itemDTO = new PedidoDTO.ItemDTO();
        itemDTO.setProdutoId(1L);
        itemDTO.setQuantidade(3L);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setItens(Arrays.asList(itemDTO));

        Usuario comprador = new Usuario();
        comprador.setId(1L);

        when(produtoService.findById(1L)).thenReturn(Optional.of(produto));

        Pedido pedido = pedidoService.createPedidoFromDTO(pedidoDTO, comprador);

        assertNotNull(pedido);
        assertEquals(1, pedido.getItens().size());
        assertEquals(comprador, pedido.getComprador());
    }

    @Test
    void deveDeletarPedido() {
        Long id = 1L;
        doNothing().when(pedidoRepository).deleteById(id);

        pedidoService.delete(id);

        verify(pedidoRepository, times(1)).deleteById(id);
    }

    @Test
    void deveBuscarPedidoPorId() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void deveListarTodosPedidos() {
        List<Pedido> pedidos = Arrays.asList(new Pedido(), new Pedido());
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<Pedido> resultado = pedidoService.listAll();

        assertEquals(2, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }
}
