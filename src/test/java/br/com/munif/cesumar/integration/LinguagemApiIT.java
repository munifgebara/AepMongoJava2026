package br.com.munif.cesumar.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.munif.cesumar.dto.LinguagemCreateRequest;
import br.com.munif.cesumar.dto.LinguagemUpdateRequest;
import br.com.munif.cesumar.model.Linguagem;
import br.com.munif.cesumar.repository.LinguagemRepository;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LinguagemApiIT {

    private static final String BASE_PATH = "/api/linguagens";

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LinguagemRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveInserirDocumentoPelaApi() throws Exception {
        LinguagemCreateRequest request = new LinguagemCreateRequest(
                "Java", LocalDate.of(1995, 5, 23), "James Gosling");

        String responseBody = mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Java"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(responseBody);
        assertNotNull(repository.findById(response.get("id").asText()).orElse(null));
    }

    @Test
    void deveRecuperarDocumentoPelaApi() throws Exception {
        repository.save(java());

        mockMvc.perform(get(BASE_PATH + "/java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("java"))
                .andExpect(jsonPath("$.nome").value("Java"))
                .andExpect(jsonPath("$.dataCriacao").value("1995-05-23"))
                .andExpect(jsonPath("$.autor").value("James Gosling"));
    }

    @Test
    void deveListarDocumentosComProjecaoResumida() throws Exception {
        repository.saveAll(List.of(
                java(),
                new Linguagem("python", "Python", LocalDate.of(1991, 2, 20), "Guido van Rossum")));

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].nome").exists())
                .andExpect(jsonPath("$[0].autor").doesNotExist())
                .andExpect(jsonPath("$[0].dataCriacao").doesNotExist());
    }

    @Test
    void deveAtualizarDocumentoPelaApiPreservandoId() throws Exception {
        repository.save(java());
        LinguagemUpdateRequest request = new LinguagemUpdateRequest(
                "Java atualizado", LocalDate.of(1995, 5, 23), "James Gosling");

        mockMvc.perform(put(BASE_PATH + "/java")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("java"))
                .andExpect(jsonPath("$.nome").value("Java atualizado"));

        Linguagem atualizada = repository.findById("java").orElseThrow();
        assertEquals("Java atualizado", atualizada.getNome());
    }

    @Test
    void deveExcluirDocumentoPelaApi() throws Exception {
        repository.save(java());

        mockMvc.perform(delete(BASE_PATH + "/java"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(repository.existsById("java"));
    }

    private Linguagem java() {
        return new Linguagem("java", "Java", LocalDate.of(1995, 5, 23), "James Gosling");
    }
}
