package br.com.munif.cesumar.documentation;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.munif.cesumar.service.LinguagemService;

@SpringBootTest(properties = "spring.autoconfigure.exclude="
        + "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration")
@AutoConfigureMockMvc
class ProjectWebPagesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinguagemService service;

    @Test
    void deveDisponibilizarPaginaInicialComOsDoisAcessos() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));

        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(allOf(
                        containsString("AEP Mongo Java 2026"),
                        containsString("href=\"/docs\""),
                        containsString("href=\"/crud.html\""))));
    }

    @Test
    void deveDisponibilizarCrudEmArquivoUnicoComJavaScriptNativo() throws Exception {
        mockMvc.perform(get("/crud.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(allOf(
                        containsString("id=\"form-linguagem\""),
                        containsString("const API_URL = \"/api/linguagens\""),
                        containsString("fetch("),
                        containsString("id ? \"PUT\" : \"POST\""),
                        containsString("method: \"DELETE\""))));
    }

    @Test
    void deveDisponibilizarDocumentacaoVisualEmDocs() throws Exception {
        mockMvc.perform(get("/docs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", endsWith("/swagger-ui/index.html")));
    }

    @Test
    void deveGerarContratoOpenApiDosEndpointsDeLinguagens() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.info.title").value("API de Linguagens de Programação"))
                .andExpect(jsonPath("$.info.version").value("v1"))
                .andExpect(jsonPath("$.paths['/api/linguagens']").exists())
                .andExpect(jsonPath("$.paths['/api/linguagens/{id}']").exists());
    }
}
