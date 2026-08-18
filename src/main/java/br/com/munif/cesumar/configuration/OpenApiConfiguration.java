package br.com.munif.cesumar.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI linguagemOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Linguagens de Programação")
                        .description("API REST didática da disciplina de Paradigmas de Programação")
                        .version("v1"));
    }
}
