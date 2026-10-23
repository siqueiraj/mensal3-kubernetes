package app.controller;

import app.config.JwtServiceGenerator;
import app.entity.Usuario;
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
    controllers = UsuarioController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtServiceGenerator jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveSalvarUsuarioComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");

        Mockito.when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");

        Mockito.when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/usuarios/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosUsuarios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Mockito.when(usuarioService.listAll()).thenReturn(Collections.singletonList(usuario));

        mockMvc.perform(get("/api/usuarios/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoBuscarUsuarioPorId() throws Exception {
        Mockito.when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/findById/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarUsuarioComSucesso() throws Exception {
        doNothing().when(usuarioService).delete(1L);

        mockMvc.perform(delete("/api/usuarios/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveLogarComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("123456");
        usuario.setTipo(Usuario.TipoUsuario.USUARIO);

        Mockito.when(usuarioService.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        Mockito.when(jwtService.generateToken(usuario.getId(), usuario.getEmail(), usuario.getTipo().name()))
                .thenReturn("fake-jwt-token");

        Usuario loginData = new Usuario();
        loginData.setEmail("teste@teste.com");
        loginData.setSenha("123456");

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token"));
    }

    @Test
    void deveRetornarUnauthorizedQuandoLoginInvalido() throws Exception {
        Mockito.when(usuarioService.findByEmail("teste@teste.com")).thenReturn(Optional.empty());

        Usuario loginData = new Usuario();
        loginData.setEmail("teste@teste.com");
        loginData.setSenha("123456");

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarUnauthorizedQuandoSenhaIncorreta() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("senhaCorreta");

        Mockito.when(usuarioService.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));

        Usuario loginData = new Usuario();
        loginData.setEmail("teste@teste.com");
        loginData.setSenha("senhaErrada");

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isUnauthorized());
    }
}
