package br.com.munif.cesumar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.munif.cesumar.dto.LinguagemCreateRequest;
import br.com.munif.cesumar.dto.LinguagemResponse;
import br.com.munif.cesumar.dto.LinguagemSummaryResponse;
import br.com.munif.cesumar.dto.LinguagemUpdateRequest;
import br.com.munif.cesumar.exception.LinguagemNotFoundException;
import br.com.munif.cesumar.mapper.LinguagemMapper;
import br.com.munif.cesumar.model.Linguagem;
import br.com.munif.cesumar.repository.LinguagemRepository;

@ExtendWith(MockitoExtension.class)
class LinguagemServiceTest {

    @Mock
    private LinguagemRepository repository;

    @Mock
    private LinguagemMapper mapper;

    @InjectMocks
    private LinguagemService service;

    @Test
    void deveListarLinguagensResumidas() {
        Linguagem java = linguagem("java", "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        Linguagem python = linguagem("python", "Python", LocalDate.of(1991, 2, 20), "Guido van Rossum");
        LinguagemSummaryResponse javaSummary = new LinguagemSummaryResponse("java", "Java");
        LinguagemSummaryResponse pythonSummary = new LinguagemSummaryResponse("python", "Python");
        when(repository.findAll()).thenReturn(List.of(java, python));
        when(mapper.toSummaryResponse(java)).thenReturn(javaSummary);
        when(mapper.toSummaryResponse(python)).thenReturn(pythonSummary);

        List<LinguagemSummaryResponse> resultado = service.listar();

        assertEquals(List.of(javaSummary, pythonSummary), resultado);
    }

    @Test
    void deveBuscarLinguagemExistente() {
        Linguagem java = linguagem("java", "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        LinguagemResponse response = response(java);
        when(repository.findById("java")).thenReturn(Optional.of(java));
        when(mapper.toResponse(java)).thenReturn(response);

        LinguagemResponse resultado = service.buscarPorId("java");

        assertEquals(response, resultado);
    }

    @Test
    void deveLancarExcecaoAoBuscarLinguagemInexistente() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        LinguagemNotFoundException exception = assertThrows(
                LinguagemNotFoundException.class,
                () -> service.buscarPorId("inexistente"));

        assertEquals("Linguagem não encontrada: inexistente", exception.getMessage());
        verifyNoInteractions(mapper);
    }

    @Test
    void deveCriarLinguagem() {
        LinguagemCreateRequest request = new LinguagemCreateRequest(
                "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        Linguagem semId = linguagem(null, "Java", request.dataCriacao(), request.autor());
        Linguagem salva = linguagem("java", "Java", request.dataCriacao(), request.autor());
        LinguagemResponse response = response(salva);
        when(mapper.toModel(request)).thenReturn(semId);
        when(repository.save(semId)).thenReturn(salva);
        when(mapper.toResponse(salva)).thenReturn(response);

        LinguagemResponse resultado = service.criar(request);

        assertEquals(response, resultado);
        verify(repository).save(semId);
    }

    @Test
    void deveAtualizarLinguagemExistente() {
        Linguagem existente = linguagem("java", "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        LinguagemUpdateRequest request = new LinguagemUpdateRequest(
                "Java atualizado", LocalDate.of(1995, 5, 23), "James Gosling");
        LinguagemResponse response = new LinguagemResponse(
                "java", request.nome(), request.dataCriacao(), request.autor());
        when(repository.findById("java")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);
        when(mapper.toResponse(existente)).thenReturn(response);

        LinguagemResponse resultado = service.atualizar("java", request);

        assertEquals(response, resultado);
        verify(mapper).updateModel(request, existente);
        verify(repository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarLinguagemInexistente() {
        LinguagemUpdateRequest request = new LinguagemUpdateRequest(
                "Desconhecida", LocalDate.of(2000, 1, 1), "Autor");
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(
                LinguagemNotFoundException.class,
                () -> service.atualizar("inexistente", request));

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(mapper);
    }

    @Test
    void deveExcluirLinguagemExistente() {
        Linguagem java = linguagem("java", "Java", LocalDate.of(1995, 5, 23), "James Gosling");
        when(repository.findById("java")).thenReturn(Optional.of(java));

        service.excluir("java");

        verify(repository).delete(java);
    }

    @Test
    void deveLancarExcecaoAoExcluirLinguagemInexistente() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(LinguagemNotFoundException.class, () -> service.excluir("inexistente"));

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    private Linguagem linguagem(String id, String nome, LocalDate dataCriacao, String autor) {
        return new Linguagem(id, nome, dataCriacao, autor);
    }

    private LinguagemResponse response(Linguagem linguagem) {
        return new LinguagemResponse(
                linguagem.getId(),
                linguagem.getNome(),
                linguagem.getDataCriacao(),
                linguagem.getAutor());
    }
}
