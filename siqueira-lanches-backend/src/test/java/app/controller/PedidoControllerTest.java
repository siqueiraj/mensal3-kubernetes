package app.controller;

import app.config.JwtServiceGenerator;
import app.dto.PedidoDTO;
import app.entity.Pedido;
import app.entity.Usuario;
import app.service.PedidoService;
import app.service.UsuarioService;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PedidoController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private UsuarioService usuarioService;
    
    @MockBean
    private JwtServiceGenerator jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveSalvarPedidoComSucesso() throws Exception {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setCompradorId(1L);

        Usuario comprador = new Usuario();
        comprador.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(comprador));
        Mockito.when(pedidoService.createPedidoFromDTO(any(PedidoDTO.class), any(Usuario.class))).thenReturn(pedido);
        Mockito.when(pedidoService.save(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveRetornarBadRequestAoSalvarPedidoComCompradorInexistente() throws Exception {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setCompradorId(99L);

        Mockito.when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/pedidos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarPedidoComSucesso() throws Exception {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setCompradorId(1L);

        Usuario comprador = new Usuario();
        comprador.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(comprador));
        Mockito.when(pedidoService.createPedidoFromDTO(any(PedidoDTO.class), any(Usuario.class))).thenReturn(pedido);
        Mockito.when(pedidoService.save(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestAoAtualizarPedidoComCompradorInexistente() throws Exception {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setCompradorId(99L);

        Mockito.when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/pedidos/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosPedidos() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Mockito.when(pedidoService.listAll()).thenReturn(Collections.singletonList(pedido));

        mockMvc.perform(get("/api/pedidos/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPedidoPorIdComSucesso() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/api/pedidos/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoBuscarPedidoPorIdInexistente() throws Exception {
        Mockito.when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/findById/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarPedidoComSucesso() throws Exception {
        doNothing().when(pedidoService).delete(1L);

        mockMvc.perform(delete("/api/pedidos/delete/1"))
                .andExpect(status().isNoContent());
    }
}
