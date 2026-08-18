package br.com.munif.cesumar.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.munif.cesumar.dto.LinguagemCreateRequest;
import br.com.munif.cesumar.dto.LinguagemResponse;
import br.com.munif.cesumar.dto.LinguagemSummaryResponse;
import br.com.munif.cesumar.dto.LinguagemUpdateRequest;
import br.com.munif.cesumar.exception.LinguagemNotFoundException;
import br.com.munif.cesumar.service.LinguagemService;

@WebMvcTest(LinguagemController.class)
class LinguagemControllerTest {

    private static final String BASE_PATH = "/api/linguagens";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LinguagemService service;

    @Test
    void deveListarLinguagensComProjecaoResumida() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new LinguagemSummaryResponse("java", "Java"),
                new LinguagemSummaryResponse("python", "Python")));

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("java"))
                .andExpect(jsonPath("$[0].nome").value("Java"))
                .andExpect(jsonPath("$[0].dataCriacao").doesNotExist())
                .andExpect(jsonPath("$[0].autor").doesNotExist());
    }

    @Test
    void deveBuscarLinguagemExistenteComRespostaCompleta() throws Exception {
        when(service.buscarPorId("java")).thenReturn(javaResponse());

        mockMvc.perform(get(BASE_PATH + "/java"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("java"))
                .andExpect(jsonPath("$.nome").value("Java"))
                .andExpect(jsonPath("$.dataCriacao").value("1995-05-23"))
                .andExpect(jsonPath("$.autor").value("James Gosling"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarLinguagemInexistente() throws Exception {
        when(service.buscarPorId("inexistente"))
                .thenThrow(new LinguagemNotFoundException("inexistente"));

        mockMvc.perform(get(BASE_PATH + "/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Linguagem não encontrada: inexistente"))
                .andExpect(jsonPath("$.path").value(BASE_PATH + "/inexistente"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void deveCriarLinguagemValidaSemExigirIdNoCorpo() throws Exception {
        LinguagemCreateRequest request = new LinguagemCreateRequest(
                "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        when(service.criar(request)).thenReturn(javaResponse());

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "http://localhost" + BASE_PATH + "/java"))
                .andExpect(jsonPath("$.id").value("java"))
                .andExpect(jsonPath("$.nome").value("Java"));
    }

    @Test
    void deveRejeitarCriacaoInvalida() throws Exception {
        String json = """
                {
                  "nome": "   ",
                  "dataCriacao": null,
                  "autor": ""
                }
                """;

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.fieldErrors.nome").value("nome é obrigatório"))
                .andExpect(jsonPath("$.fieldErrors.dataCriacao").value("dataCriacao é obrigatória"))
                .andExpect(jsonPath("$.fieldErrors.autor").value("autor é obrigatório"));

        verify(service, never()).criar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveAtualizarLinguagemValida() throws Exception {
        LinguagemUpdateRequest request = new LinguagemUpdateRequest(
                "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        when(service.atualizar("java", request)).thenReturn(javaResponse());

        mockMvc.perform(put(BASE_PATH + "/java")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("java"))
                .andExpect(jsonPath("$.nome").value("Java"))
                .andExpect(jsonPath("$.dataCriacao").value("1995-05-23"))
                .andExpect(jsonPath("$.autor").value("James Gosling"));
    }

    @Test
    void deveRejeitarAtualizacaoInvalida() throws Exception {
        String json = """
                {
                  "nome": "",
                  "dataCriacao": null,
                  "autor": "   "
                }
                """;

        mockMvc.perform(put(BASE_PATH + "/java")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors.nome").exists())
                .andExpect(jsonPath("$.fieldErrors.dataCriacao").exists())
                .andExpect(jsonPath("$.fieldErrors.autor").exists());

        verify(service, never()).atualizar(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarLinguagemInexistente() throws Exception {
        LinguagemUpdateRequest request = new LinguagemUpdateRequest(
                "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        when(service.atualizar("inexistente", request))
                .thenThrow(new LinguagemNotFoundException("inexistente"));

        mockMvc.perform(put(BASE_PATH + "/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value(BASE_PATH + "/inexistente"));
    }

    @Test
    void deveExcluirLinguagemExistente() throws Exception {
        doNothing().when(service).excluir("java");

        mockMvc.perform(delete(BASE_PATH + "/java"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).excluir("java");
    }

    @Test
    void deveRetornarNotFoundAoExcluirLinguagemInexistente() throws Exception {
        doThrow(new LinguagemNotFoundException("inexistente"))
                .when(service).excluir("inexistente");

        mockMvc.perform(delete(BASE_PATH + "/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Linguagem não encontrada: inexistente"));
    }

    private LinguagemResponse javaResponse() {
        return new LinguagemResponse(
                "java", "Java", LocalDate.of(1995, 5, 23), "James Gosling");
    }
}
