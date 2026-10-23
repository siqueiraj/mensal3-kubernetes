package app.controller;

import app.config.JwtServiceGenerator;
import app.entity.Endereco;
import app.service.EnderecoService;
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
    controllers = EnderecoController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoService enderecoService;
    
    @MockBean
    private JwtServiceGenerator jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveSalvarEnderecoComSucesso() throws Exception {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCidade("São Paulo");

        Mockito.when(enderecoService.save(any(Endereco.class))).thenReturn(endereco);

        mockMvc.perform(post("/api/enderecos/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cidade").value("São Paulo"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarEnderecoComSucesso() throws Exception {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCidade("Campinas");

        Mockito.when(enderecoService.save(any(Endereco.class))).thenReturn(endereco);

        mockMvc.perform(put("/api/enderecos/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cidade").value("Campinas"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveListarTodosEnderecos() throws Exception {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCidade("Rio de Janeiro");

        Mockito.when(enderecoService.listAll()).thenReturn(Collections.singletonList(endereco));

        mockMvc.perform(get("/api/enderecos/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cidade").value("Rio de Janeiro"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveBuscarEnderecoPorIdComSucesso() throws Exception {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCidade("Curitiba");

        Mockito.when(enderecoService.findById(1L)).thenReturn(Optional.of(endereco));

        mockMvc.perform(get("/api/enderecos/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cidade").value("Curitiba"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveRetornarNotFoundAoBuscarEnderecoPorId() throws Exception {
        Mockito.when(enderecoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/enderecos/findById/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deveDeletarEnderecoComSucesso() throws Exception {
        doNothing().when(enderecoService).delete(1L);

        mockMvc.perform(delete("/api/enderecos/delete/1"))
                .andExpect(status().isNoContent());
    }
}
