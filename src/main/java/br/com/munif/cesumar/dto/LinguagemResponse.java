package br.com.munif.cesumar.dto;

import java.time.LocalDate;

public record LinguagemResponse(
        String id,
        String nome,
        LocalDate dataCriacao,
        String autor) {
}
