package br.com.munif.cesumar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LinguagemCreateRequest(
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotNull(message = "dataCriacao é obrigatória")
        LocalDate dataCriacao,

        @NotBlank(message = "autor é obrigatório")
        String autor) {
}
