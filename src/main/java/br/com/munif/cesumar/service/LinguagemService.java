package br.com.munif.cesumar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.munif.cesumar.dto.LinguagemCreateRequest;
import br.com.munif.cesumar.dto.LinguagemResponse;
import br.com.munif.cesumar.dto.LinguagemSummaryResponse;
import br.com.munif.cesumar.dto.LinguagemUpdateRequest;
import br.com.munif.cesumar.exception.LinguagemNotFoundException;
import br.com.munif.cesumar.mapper.LinguagemMapper;
import br.com.munif.cesumar.model.Linguagem;
import br.com.munif.cesumar.repository.LinguagemRepository;

@Service
public class LinguagemService {

    private final LinguagemRepository repository;
    private final LinguagemMapper mapper;

    public LinguagemService(LinguagemRepository repository, LinguagemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<LinguagemSummaryResponse> listar() {
        return repository.findAll().stream()
                .map(mapper::toSummaryResponse)
                .toList();
    }

    public LinguagemResponse buscarPorId(String id) {
        return mapper.toResponse(buscarModelPorId(id));
    }

    public LinguagemResponse criar(LinguagemCreateRequest request) {
        Linguagem linguagem = mapper.toModel(request);
        return mapper.toResponse(repository.save(linguagem));
    }

    public LinguagemResponse atualizar(String id, LinguagemUpdateRequest request) {
        Linguagem linguagem = buscarModelPorId(id);
        mapper.updateModel(request, linguagem);
        return mapper.toResponse(repository.save(linguagem));
    }

    public void excluir(String id) {
        Linguagem linguagem = buscarModelPorId(id);
        repository.delete(linguagem);
    }

    private Linguagem buscarModelPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new LinguagemNotFoundException(id));
    }
}
