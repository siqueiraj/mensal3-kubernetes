package app.controller;

import app.config.JwtServiceGenerator;
import app.dto.PagamentoDTO;
import app.entity.Pagamento;
import app.entity.Pedido;
import app.service.PagamentoService;
import app.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PagamentoController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;

    @MockBean
    private PedidoService pedidoService;
    
    @MockBean
    private JwtServiceGenerator jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveSalvarPagamentoComSucesso() throws Exception {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setPedidoId(1L);
        dto.setValor(100.0);

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setPedido(pedido);
        pagamento.setValor(new BigDecimal("100.0"));

        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));
        Mockito.when(pagamentoService.findByPedidoId(1L)).thenReturn(Optional.empty());
        Mockito.when(pagamentoService.save(any(Pagamento.class))).thenReturn(pagamento);

        mockMvc.perform(post("/api/pagamentos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveRetornarBadRequestQuandoPedidoNaoExisteAoSalvarPagamento() throws Exception {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setPedidoId(99L);

        Mockito.when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/pagamentos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveRetornarConflictQuandoPagamentoJaExisteParaPedido() throws Exception {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setPedidoId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Pagamento pagamentoExistente = new Pagamento();
        pagamentoExistente.setId(2L);

        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));
        Mockito.when(pagamentoService.findByPedidoId(1L)).thenReturn(Optional.of(pagamentoExistente));

        mockMvc.perform(post("/api/pagamentos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarPagamentoComSucesso() throws Exception {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setPedidoId(1L);
        dto.setValor(150.0);

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setPedido(pedido);
        pagamento.setValor(new BigDecimal("150.0"));

        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));
        Mockito.when(pagamentoService.save(any(Pagamento.class))).thenReturn(pagamento);

        mockMvc.perform(put("/api/pagamentos/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoPedidoNaoExisteAoAtualizarPagamento() throws Exception {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setPedidoId(99L);

        Mockito.when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/pagamentos/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosPagamentos() throws Exception {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);

        Mockito.when(pagamentoService.listAll()).thenReturn(Collections.singletonList(pagamento));

        mockMvc.perform(get("/api/pagamentos/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPagamentoPorIdComSucesso() throws Exception {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);

        Mockito.when(pagamentoService.findById(1L)).thenReturn(Optional.of(pagamento));

        mockMvc.perform(get("/api/pagamentos/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoBuscarPagamentoPorId() throws Exception {
        Mockito.when(pagamentoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pagamentos/findById/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarPagamentoComSucesso() throws Exception {
        doNothing().when(pagamentoService).delete(1L);

        mockMvc.perform(delete("/api/pagamentos/delete/1"))
                .andExpect(status().isNoContent());
    }
}
