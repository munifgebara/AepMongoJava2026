# Decisões arquiteturais

## ADR-001 — Java 21

**Decisão:** utilizar Java 21.

**Motivação:** trabalhar com uma versão LTS moderna e tornar recursos atuais da linguagem, como records, disponíveis aos exemplos.

**Alternativas consideradas:** Java 17 e versões não LTS.

**Consequências:** o ambiente de desenvolvimento e execução precisa disponibilizar JDK 21.

## ADR-002 — Spring Boot

**Decisão:** utilizar Spring Boot 3.5.16 como base da API REST.

**Motivação:** integrar servidor HTTP, validação, persistência e testes com configuração pequena e convenções amplamente adotadas.

**Alternativas consideradas:** configurar Spring Framework manualmente ou usar outro framework web.

**Consequências:** versões das bibliotecas principais serão gerenciadas pelo Spring Boot e o projeto seguirá suas convenções. A linha 3.5 foi escolhida por ser estável, madura e compatível com Java 21, evitando uma migração didaticamente desnecessária para uma nova geração principal.

## ADR-003 — MongoDB

**Decisão:** persistir linguagens de programação em MongoDB.

**Motivação:** demonstrar persistência NoSQL orientada a documentos na disciplina.

**Alternativas consideradas:** banco relacional e armazenamento somente em memória.

**Consequências:** a aplicação usará Spring Data MongoDB e o desenvolvimento local aproveitará o Compose existente.

## ADR-004 — Camadas Controller, Service e Repository

**Decisão:** separar protocolo HTTP, casos de uso e persistência em camadas explícitas.

**Motivação:** tornar responsabilidade, composição e injeção de dependência observáveis no código didático.

**Alternativas consideradas:** Controller acessando Repository diretamente e arquiteturas com mais abstrações.

**Consequências:** Controllers permanecem pequenos e Services concentram os casos de uso, sem criar interfaces artificiais.

## ADR-005 — DTOs específicos por operação

**Decisão:** criar contratos distintos para criação, atualização, resposta completa e resumo.

**Motivação:** DTOs representam casos de uso e devem evoluir independentemente da persistência e uns dos outros.

**Alternativas consideradas:** um DTO universal e exposição direta do documento MongoDB.

**Consequências:** há mais tipos pequenos, mas os contratos HTTP ficam explícitos e protegidos contra alterações acidentais.

## ADR-006 — Mapping explícito

**Decisão:** implementar conversões em classes Mapper com Java explícito.

**Motivação:** permitir que estudantes visualizem a passagem de dados entre contratos e persistência.

**Alternativas consideradas:** MapStruct, ModelMapper e conversões espalhadas por Controllers e Services.

**Consequências:** existe algum código de conversão manual, centralizado e fácil de depurar.

## ADR-007 — Testcontainers

**Decisão:** executar testes de integração contra MongoDB real criado por Testcontainers.

**Motivação:** validar comportamento real do driver e do banco sem depender da máquina ou do Compose de desenvolvimento.

**Alternativas consideradas:** MongoDB local, banco compartilhado e implementações fake embarcadas.

**Consequências:** testes de integração são reproduzíveis, mas exigem Docker disponível.

## ADR-008 — Java Records para DTOs

**Decisão:** utilizar records quando forem adequados aos DTOs imutáveis.

**Motivação:** expressar contratos de dados concisos e imutáveis com recurso nativo do Java 21.

**Alternativas consideradas:** classes mutáveis com getters e setters e Lombok.

**Consequências:** DTOs têm pouca cerimônia e não dependem de geração de código externa.

## ADR-009 — Springdoc OpenAPI

**Decisão:** gerar o contrato OpenAPI e a documentação visual com Springdoc OpenAPI 2.9.0, expondo a interface em `/docs`.

**Motivação:** oferecer uma página web interativa que permaneça sincronizada com os Controllers, DTOs e validações da aplicação.

**Alternativas consideradas:** manter somente exemplos manuais no README, gerar documentação estática com Spring REST Docs ou configurar Swagger UI manualmente.

**Consequências:** a aplicação passa a expor a interface visual em `/docs` e o contrato JSON em `/v3/api-docs`, com uma dependência adicional compatível com Spring Boot 3.5.16.

## ADR-010 — Cliente web sem framework

**Decisão:** servir uma página inicial em `/` e implementar o cliente CRUD em um único `crud.html`, usando somente HTML, CSS e JavaScript com `fetch`.

**Motivação:** permitir que os estudantes acompanhem diretamente a relação entre eventos da interface, requisições HTTP, DTOs e respostas da API.

**Alternativas consideradas:** React, Vue, Angular, bibliotecas de requisição e templates renderizados no servidor.

**Consequências:** não há dependência ou processo de build de frontend. A interface é propositalmente pequena e consome somente os contratos REST públicos.

## ADR-011 — MongoDB 7.0 no ambiente de desenvolvimento

**Decisão:** utilizar `mongo:7.0` como versão padrão no Compose de desenvolvimento, mantendo a substituição pela variável `MONGO_VERSION`.

**Motivação:** a imagem `mongo:8.0` recusa iniciar em alguns hosts com kernel Linux 6.19 ou mais novo. A versão 7.0 também é a utilizada pelos testes de integração e torna o exemplo mais reproduzível entre as máquinas da turma.

**Alternativas consideradas:** exigir atualização do kernel, manter 8.0 e documentar um comando alternativo ou usar uma tag sem versão fixa.

**Consequências:** `docker compose up -d` funciona no ambiente validado sem parâmetros adicionais. Máquinas compatíveis ainda podem selecionar outra versão com `MONGO_VERSION`, e a tag `latest` continua evitada.

## ADR-012 — Volume de dados exclusivo do projeto

**Decisão:** montar o volume Docker nomeado `aepmongojava2026_mongo-data` em `/data/db`, permitindo substituir seu nome com `MONGO_DATA_VOLUME`.

**Motivação:** manter os dados isolados de qualquer MongoDB instalado no host e oferecer o mesmo Compose no Linux e no Docker Desktop do Windows. A imagem oficial do MongoDB recomenda volumes nomeados no Windows, pois bind mounts do host podem ser incompatíveis com seus arquivos mapeados em memória.

**Alternativas consideradas:** bind mount em uma pasta do repositório e caminhos absolutos específicos de cada sistema operacional.

**Consequências:** o Docker gerencia a localização física dos dados; `docker volume inspect aepmongojava2026_mongo-data` permite consultá-la. O volume não é removido por `docker compose down`, mas é apagado por `docker compose down --volumes`.
