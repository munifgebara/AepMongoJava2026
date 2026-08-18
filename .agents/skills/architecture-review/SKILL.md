---
name: architecture-review
description: Revisar uma funcionalidade Spring Boot deste projeto antes de considerá-la concluída. Usar após implementar ou modificar Controller, DTO, Mapper, Service, Repository, Model, exceções, configuração, dependências ou testes.
---

# Architecture Review

## Fluxo esperado

Confirmar o caminho de entrada:

```text
Controller -> DTO -> Service -> Repository -> MongoDB
```

Confirmar que Models retornam pela conversão explícita:

```text
MongoDB -> Model -> Service -> Mapper -> Response DTO -> Controller
```

## Checklist

### Controller

- Identificar lógica de negócio, acesso direto ao Repository ou `try/catch` usado para fluxo normal.
- Confirmar DTOs, validação e status HTTP semanticamente corretos.

### DTO e Model

- Identificar Model exposto diretamente.
- Confirmar que cada DTO representa um caso de uso claro e não é genérico apenas por conveniência.
- Verificar validação de entrada e isolamento da persistência.

### Service, Repository e Mapper

- Confirmar que o Service concentra casos de uso e regras.
- Limitar o Repository a persistência e consultas.
- Centralizar conversões no Mapper e preservar IDs em atualizações.
- Identificar acoplamento, duplicação ou métodos excessivamente grandes.

### Exceptions

- Procurar exceções relevantes não tratadas.
- Confirmar resposta de erro consistente e centralizada.

### Dependencies e código

- Identificar dependências e abstrações desnecessárias.
- Procurar nomenclatura inconsistente, imports não utilizados, comentários redundantes e complexidade evitável.
- Evitar interfaces artificiais, Lombok e frameworks de mapping.

### Tests

- Confirmar testes para comportamento novo ou corrigido.
- Identificar testes que verificam implementação trivial sem comportamento relevante.
- Cobrir sucesso, ausência, validação e contratos HTTP aplicáveis.
- Garantir independência de ordem, estado externo e Compose de desenvolvimento.

### Documentation

- Conferir `README.md`, `HARNESS.md`, arquitetura, decisões e TODO contra o código atual.
- Registrar limitações reais e comandos não executados.

## Conclusão

Corrigir problemas encontrados, executar `./mvnw clean verify` e revisar o diff final. Não declarar conclusão com testes falhando ou sem explicar uma limitação de ambiente.
