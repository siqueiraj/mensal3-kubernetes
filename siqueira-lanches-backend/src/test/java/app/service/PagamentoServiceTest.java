package app.service;

import app.entity.Pagamento;
import app.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @InjectMocks
    private PagamentoService pagamentoService;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodosPagamentos() {
        List<Pagamento> pagamentos = Arrays.asList(new Pagamento(), new Pagamento());
        when(pagamentoRepository.findAll()).thenReturn(pagamentos);

        List<Pagamento> resultado = pagamentoService.listAll();

        assertEquals(2, resultado.size());
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarPagamentoPorId() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        Optional<Pagamento> resultado = pagamentoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(pagamentoRepository, times(1)).findById(1L);
    }

    @Test
    void deveBuscarPagamentoPorPedidoId() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(2L);
        when(pagamentoRepository.findByPedidoId(10L)).thenReturn(Optional.of(pagamento));

        Optional<Pagamento> resultado = pagamentoService.findByPedidoId(10L);

        assertTrue(resultado.isPresent());
        assertEquals(2L, resultado.get().getId());
        verify(pagamentoRepository, times(1)).findByPedidoId(10L);
    }

    @Test
    void deveSalvarPagamento() {
        Pagamento pagamento = new Pagamento();
        pagamento.setValor(new BigDecimal("100.0"));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.save(pagamento);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("100.0"), resultado.getValor());
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void deveDeletarPagamento() {
        Long id = 1L;
        doNothing().when(pagamentoRepository).deleteById(id);

        pagamentoService.delete(id);

        verify(pagamentoRepository, times(1)).deleteById(id);
    }
}
