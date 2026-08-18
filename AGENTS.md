# Orientações para agentes de IA

## Objetivo do projeto

Este é um projeto didático da disciplina de Paradigmas de Programação. Ele deve apresentar, de forma pequena e explícita, boas práticas de orientação a objetos, separação de responsabilidades, contratos HTTP, persistência NoSQL e testes automatizados.

O primeiro domínio é uma API REST CRUD de linguagens de programação armazenadas no MongoDB.

## Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data MongoDB
- Jakarta Validation
- Springdoc OpenAPI
- Maven
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- Testcontainers
- MongoDB
- Mongo Express
- Docker Compose existente em `compose.yaml`

## Package base

Use `br.com.munif.cesumar`. Organize os recursos abaixo dele por responsabilidade, preservando os nomes em português adotados pelo domínio.

## Arquitetura

- **Controller:** traduz HTTP em chamadas de casos de uso, valida DTOs de entrada e define respostas HTTP.
- **DTO:** representa contratos de entrada e saída específicos da API.
- **Mapper:** concentra conversões explícitas entre DTOs e Models.
- **Service:** implementa casos de uso e regras de negócio.
- **Repository:** limita-se a persistência e consultas com Spring Data.
- **Model:** representa documentos persistidos no MongoDB, sem funcionar como contrato HTTP.
- **Exception:** representa falhas do domínio e seu tratamento HTTP centralizado.
- **Configuration:** contém configuração de infraestrutura e dados de desenvolvimento.

Fluxo principal:

```text
Controller -> DTO -> Service -> Repository -> MongoDB
MongoDB -> Model -> Service -> Mapper -> Response DTO -> Controller
```

## Regras obrigatórias

1. Antes de implementar uma alteração, examine os arquivos existentes e preserve os padrões arquiteturais e de nomenclatura já utilizados no projeto.
2. Preserve padrões arquiteturais e de nomenclatura já adotados.
3. Não exponha diretamente documentos MongoDB pela API REST.
4. Controllers trabalham com DTOs.
5. Models de persistência não são contratos HTTP.
6. DTOs devem representar casos de uso específicos.
7. Não reutilize automaticamente o mesmo DTO para criação, atualização e resposta apenas porque possuem campos semelhantes.
8. Controllers devem possuir pouca ou nenhuma regra de negócio.
9. Regras de negócio pertencem à camada Service.
10. Repositories são responsáveis exclusivamente por persistência e consulta.
11. Mapeamentos entre DTO e Model devem ficar isolados.
12. Não adicione abstrações sem necessidade.
13. Não introduza frameworks ou dependências sem justificar e registrar decisões relevantes.
14. Toda funcionalidade nova deve possuir testes automatizados quando tecnicamente aplicável.
15. Toda correção de comportamento deve possuir teste que demonstre o comportamento corrigido.
16. Antes de considerar uma tarefa concluída, execute os testes.
17. Nunca remova testes apenas para conseguir um build verde.
18. Evite comentários que apenas repitam o código.
19. Prefira código legível a comentários explicativos excessivos.
20. Não use Lombok apenas para reduzir algumas linhas de código.
21. Prefira Java moderno quando isso tornar o código mais claro.
22. DTOs podem utilizar `record` quando apropriado.
23. Não modifique `compose.yaml` sem necessidade expressamente indicada.
24. Não coloque credenciais reais no Git.
25. Mantenha documentação sincronizada com decisões arquiteturais relevantes.
26. Use injeção por construtor; não use injeção em campos.
27. Não crie interfaces `Service`/`ServiceImpl` ou `Mapper`/`MapperImpl` sem um problema concreto que as justifique.

> DTOs representam contratos de casos de uso, não entidades de persistência.
> Não reutilize DTO apenas por coincidência estrutural.

> Antes de implementar uma alteração, examine os arquivos existentes e preserve
> os padrões arquiteturais e de nomenclatura já utilizados no projeto.

> Toda funcionalidade nova ou correção de comportamento deve possuir testes
> automatizados correspondentes quando tecnicamente aplicável.

## Processo de trabalho

1. Ler este arquivo e os arquivos na área afetada.
2. Usar as Skills locais aplicáveis em `.agents/skills/`.
3. Implementar a menor solução coerente com a arquitetura.
4. Adicionar ou atualizar testes comportamentais.
5. Executar os comandos pertinentes documentados em `HARNESS.md`.
6. Aplicar a revisão de `.agents/skills/architecture-review/SKILL.md`.
7. Atualizar README, decisões e arquitetura quando a alteração afetá-los.
