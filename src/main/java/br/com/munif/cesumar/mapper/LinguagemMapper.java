package br.com.munif.cesumar.mapper;

import org.springframework.stereotype.Component;

import br.com.munif.cesumar.dto.LinguagemCreateRequest;
import br.com.munif.cesumar.dto.LinguagemResponse;
import br.com.munif.cesumar.dto.LinguagemSummaryResponse;
import br.com.munif.cesumar.dto.LinguagemUpdateRequest;
import br.com.munif.cesumar.model.Linguagem;

@Component
public class LinguagemMapper {

    public Linguagem toModel(LinguagemCreateRequest request) {
        return new Linguagem(null, request.nome(), request.dataCriacao(), request.autor());
    }

    public void updateModel(LinguagemUpdateRequest request, Linguagem linguagem) {
        linguagem.setNome(request.nome());
        linguagem.setDataCriacao(request.dataCriacao());
        linguagem.setAutor(request.autor());
    }

    public LinguagemResponse toResponse(Linguagem linguagem) {
        return new LinguagemResponse(
                linguagem.getId(),
                linguagem.getNome(),
                linguagem.getDataCriacao(),
                linguagem.getAutor());
    }

    public LinguagemSummaryResponse toSummaryResponse(Linguagem linguagem) {
        return new LinguagemSummaryResponse(linguagem.getId(), linguagem.getNome());
    }
}
