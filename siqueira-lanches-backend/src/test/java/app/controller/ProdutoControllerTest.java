package app.controller;

import app.config.JwtServiceGenerator;
import app.entity.Produto;
import app.service.ProdutoService;
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
    controllers = ProdutoController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;
    
    @MockBean
    private JwtServiceGenerator jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveSalvarProdutoComSucesso() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        Mockito.when(produtoService.save(any(Produto.class))).thenReturn(produto);

        mockMvc.perform(post("/api/produtos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarProdutoComSucesso() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Atualizado");

        Mockito.when(produtoService.save(any(Produto.class))).thenReturn(produto);

        mockMvc.perform(put("/api/produtos/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto Atualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosProdutos() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        Mockito.when(produtoService.listAll()).thenReturn(Collections.singletonList(produto));

        mockMvc.perform(get("/api/produtos/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        Mockito.when(produtoService.findById(1L)).thenReturn(Optional.of(produto));

        mockMvc.perform(get("/api/produtos/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoBuscarProdutoPorId() throws Exception {
        Mockito.when(produtoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/produtos/findById/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarProdutoComSucesso() throws Exception {
        doNothing().when(produtoService).delete(1L);

        mockMvc.perform(delete("/api/produtos/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarProdutoPorNomeComSucesso() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        Mockito.when(produtoService.findByNome("Produto Teste"))
                .thenReturn(Collections.singletonList(produto));

        mockMvc.perform(get("/api/produtos/findByNome/Produto Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundQuandoBuscarProdutoPorNomeInexistente() throws Exception {
        Mockito.when(produtoService.findByNome("Inexistente"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/produtos/findByNome/Inexistente"))
                .andExpect(status().isNotFound());
    }
}
