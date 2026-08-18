---
name: spring-crud
description: Implementar ou evoluir recursos CRUD Spring Boot neste projeto, incluindo Model, Repository, DTOs, Mapper, Service, Controller, exceções e testes. Usar ao criar endpoints, operações de persistência MongoDB ou um novo domínio da API.
---

# Spring CRUD

## Preparação

1. Ler `AGENTS.md`, `docs/architecture.md` e os arquivos existentes da área afetada.
2. Procurar um CRUD já implementado e adotá-lo como referência de packages, nomes, contratos e testes.
3. Confirmar o package base e não criar uma arquitetura paralela.
4. Implementar apenas abstrações necessárias ao caso de uso atual.

## Fluxo de implementação

Seguir esta ordem:

```text
Model
Repository
Request DTOs
Response DTOs
Mapper
Service
Controller
Exceptions
Tests
```

### Model

- Representar a persistência com `@Document`.
- Manter o Model isolado da fronteira HTTP.
- Nunca receber nem devolver um `@Document` diretamente no Controller.

### Repository

- Estender a abstração apropriada do Spring Data MongoDB.
- Limitar-se a persistência e consultas.
- Não implementar regra de negócio no Repository.

### DTOs

- Criar contratos por caso de uso.
- Separar Request DTO de Response DTO, pois possuem responsabilidades diferentes.
- Permitir que POST e PUT tenham DTOs diferentes, mesmo quando seus campos coincidirem hoje.
- Aplicar Jakarta Validation ao contrato de entrada.
- Não incluir identificador no corpo se ele vier do path.
- Preferir `record` quando representar dados imutáveis com clareza.

### Mapper

- Centralizar conversões DTO/Model em Java explícito.
- Não acessar Repository nem implementar regra de negócio.
- Preservar o identificador persistido durante atualizações.

### Service

- Implementar os casos de uso e regras de negócio.
- Receber dependências por construtor.
- Traduzir ausência de dados em exceções específicas do domínio.

### Controller

- Trabalhar somente com DTOs.
- Delegar regras e persistência ao Service.
- Usar `@Valid` nos contratos de entrada.
- Retornar status HTTP semanticamente corretos: `200` para leitura/atualização, `201` e `Location` para criação, `204` para exclusão e `404` para recurso ausente.

### Exceptions

- Criar exceções específicas e tratá-las centralmente com `@RestControllerAdvice`.
- Não usar `try/catch` no Controller para fluxo HTTP normal.
- Manter uma estrutura de erro consistente e tipada.

### Tests

- Aplicar a Skill `testing`.
- Cobrir casos felizes, recursos ausentes, validações e contratos HTTP.
- Executar a suíte antes de concluir.

## Revisão

Aplicar a Skill `architecture-review` e sincronizar README e decisões relevantes.
