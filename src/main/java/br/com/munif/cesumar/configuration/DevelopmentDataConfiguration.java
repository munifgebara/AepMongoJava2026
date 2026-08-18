package br.com.munif.cesumar.configuration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.munif.cesumar.model.Linguagem;
import br.com.munif.cesumar.repository.LinguagemRepository;

@Configuration
@Profile("dev")
public class DevelopmentDataConfiguration {

    @Bean
    CommandLineRunner loadDevelopmentData(LinguagemRepository repository) {
        return args -> sampleLanguages().stream()
                .filter(linguagem -> !repository.existsById(linguagem.getId()))
                .forEach(repository::save);
    }

    private List<Linguagem> sampleLanguages() {
        return List.of(
                new Linguagem("java", "Java", LocalDate.of(1995, 5, 23), "James Gosling"),
                new Linguagem("python", "Python", LocalDate.of(1991, 2, 20), "Guido van Rossum"),
                new Linguagem("c", "C", LocalDate.of(1972, 1, 1), "Dennis Ritchie"),
                new Linguagem("rust", "Rust", LocalDate.of(2010, 7, 7), "Graydon Hoare"),
                new Linguagem("javascript", "JavaScript", LocalDate.of(1995, 12, 4), "Brendan Eich"));
    }
}
