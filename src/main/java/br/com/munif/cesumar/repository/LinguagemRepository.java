package br.com.munif.cesumar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.munif.cesumar.model.Linguagem;

public interface LinguagemRepository extends MongoRepository<Linguagem, String> {
}
