package app.controller;

import app.config.JwtServiceGenerator;
import app.entity.Avaliacao;
import app.service.AvaliacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

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
		controllers = AvaliacaoController.class,
		excludeAutoConfiguration = {
				org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
				org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
		}
		)
@AutoConfigureMockMvc(addFilters = false)
class AvaliacaoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AvaliacaoService avaliacaoService;
	
	@MockBean
    private JwtServiceGenerator jwtService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(roles = "CLIENTE")
	void deveSalvarAvaliacaoComSucesso() throws Exception {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setId(1L);
		avaliacao.setComentario("Ótimo produto");

		Mockito.when(avaliacaoService.save(any(Avaliacao.class))).thenReturn(avaliacao);

		mockMvc.perform(post("/api/avaliacoes/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(avaliacao)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.comentario").value("Ótimo produto"));
	}

	@Test
	@WithMockUser(roles = "CLIENTE")
	void deveAtualizarAvaliacaoComSucesso() throws Exception {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setId(1L);
		avaliacao.setComentario("Comentário atualizado");

		Mockito.when(avaliacaoService.save(any(Avaliacao.class))).thenReturn(avaliacao);

		mockMvc.perform(put("/api/avaliacoes/update/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(avaliacao)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.comentario").value("Comentário atualizado"));
	}

	@Test
	@WithMockUser(roles = "CLIENTE")
	void deveListarTodasAvaliacoes() throws Exception {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setId(1L);
		avaliacao.setComentario("Produto bom");

		Mockito.when(avaliacaoService.listAll()).thenReturn(Collections.singletonList(avaliacao));

		mockMvc.perform(get("/api/avaliacoes/listAll"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id").value(1L))
		.andExpect(jsonPath("$[0].comentario").value("Produto bom"));
	}

	@Test
	@WithMockUser(roles = "CLIENTE")
	void deveBuscarAvaliacaoPorIdComSucesso() throws Exception {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setId(1L);
		avaliacao.setComentario("Muito bom");

		Mockito.when(avaliacaoService.findById(1L)).thenReturn(Optional.of(avaliacao));

		mockMvc.perform(get("/api/avaliacoes/findById/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.comentario").value("Muito bom"));
	}

	@Test
	@WithMockUser(roles = "CLIENTE")
	void deveRetornarNotFoundAoBuscarAvaliacaoPorId() throws Exception {
		Mockito.when(avaliacaoService.findById(99L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/api/avaliacoes/findById/99"))
		.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(roles = "CLIENTE")
	void deveDeletarAvaliacaoComSucesso() throws Exception {
		doNothing().when(avaliacaoService).delete(1L);

		mockMvc.perform(delete("/api/avaliacoes/delete/1"))
		.andExpect(status().isNoContent());
	}
}
