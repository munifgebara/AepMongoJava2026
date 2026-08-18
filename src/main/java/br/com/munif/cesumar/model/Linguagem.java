package br.com.munif.cesumar.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "linguagens")
public class Linguagem {

    @Id
    private String id;
    private String nome;
    private LocalDate dataCriacao;
    private String autor;

    public Linguagem(String id, String nome, LocalDate dataCriacao, String autor) {
        this.id = id;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.autor = autor;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
