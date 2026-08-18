package br.com.munif.cesumar.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.munif.cesumar.dto.LinguagemCreateRequest;
import br.com.munif.cesumar.dto.LinguagemResponse;
import br.com.munif.cesumar.dto.LinguagemSummaryResponse;
import br.com.munif.cesumar.dto.LinguagemUpdateRequest;
import br.com.munif.cesumar.service.LinguagemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/linguagens")
public class LinguagemController {

    private final LinguagemService service;

    public LinguagemController(LinguagemService service) {
        this.service = service;
    }

    @GetMapping
    public List<LinguagemSummaryResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public LinguagemResponse buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<LinguagemResponse> criar(
            @Valid @RequestBody LinguagemCreateRequest request) {
        LinguagemResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public LinguagemResponse atualizar(
            @PathVariable String id,
            @Valid @RequestBody LinguagemUpdateRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
