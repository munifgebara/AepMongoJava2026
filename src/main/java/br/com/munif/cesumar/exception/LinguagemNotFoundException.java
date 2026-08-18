package br.com.munif.cesumar.exception;

public class LinguagemNotFoundException extends RuntimeException {

    public LinguagemNotFoundException(String id) {
        super("Linguagem não encontrada: " + id);
    }
}
