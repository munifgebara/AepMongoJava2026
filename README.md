# API REST didática de linguagens de programação

Projeto da disciplina de Paradigmas de Programação para estudar orientação a objetos, separação de responsabilidades, contratos HTTP com DTOs, persistência NoSQL e testes automatizados.

O primeiro domínio é um CRUD de linguagens de programação armazenadas no MongoDB. A aplicação é pequena de propósito: as conversões e responsabilidades ficam explícitas para que possam ser acompanhadas em sala de aula.

> As credenciais do ambiente local são públicas e simples intencionalmente. Não use esta configuração em produção e não registre credenciais reais no repositório.

## Stack

- Java 21;
- Spring Boot 3.5.16;
- Spring Web;
- Spring Data MongoDB;
- Jakarta Validation;
- Maven Wrapper;
- JUnit 5, Mockito, MockMvc e Spring Boot Test;
- Testcontainers com MongoDB;
- JaCoCo;
- MongoDB e Mongo Express via Docker Compose.

## Arquitetura

O fluxo principal de uma requisição é:

```text
JSON -> Controller -> Request DTO -> Service -> Repository -> MongoDB
```

O fluxo de resposta é:

```text
MongoDB -> Model -> Service -> Mapper -> Response DTO -> Controller -> JSON
```

- `Controller` trata HTTP e delega os casos de uso;
- `DTO` define contratos específicos de entrada e saída;
- `Mapper` converte explicitamente DTOs e documentos;
- `Service` concentra os casos de uso e regras de negócio;
- `Repository` cuida somente da persistência;
- `Model` representa o documento MongoDB;
- `Exception` padroniza falhas da API;
- `Configuration` contém infraestrutura, como a carga de dados de desenvolvimento.

Consulte também [docs/architecture.md](docs/architecture.md), [docs/decisions.md](docs/decisions.md) e [docs/http-api.md](docs/http-api.md).

## Pré-requisitos

- Java 21;
- Docker;
- Docker Compose v2.

Não é necessário instalar Maven: o projeto inclui o Maven Wrapper.

```bash
java -version
docker --version
docker compose version
./mvnw -version
```

## Configuração do MongoDB

O `compose.yaml` existente define:

| Serviço | Endereço local | Credenciais iniciais |
|---|---|---|
| MongoDB | `localhost:27018` | `root` / `Mongo` |
| Mongo Express | `http://localhost:18081` | `cesumar` / `cesumar` |

As portas não usam os padrões mais disputados na máquina: externamente são `27018` e `18081`; dentro da rede Docker, os serviços continuam usando `27017` e `8081`.

O arquivo `.env` mantém os valores didáticos fora do Compose, mas eles também podem ser substituídos pelo ambiente. Por exemplo:

```bash
MONGO_PORT=37018 MONGO_ROOT_PASSWORD=OutraSenha docker compose up -d
```

No PowerShell:

```powershell
$env:MONGO_PORT="37018"
$env:MONGO_ROOT_PASSWORD="OutraSenha"
docker compose up -d
```

Se alterar usuário, senha ou porta, ajuste também `SPRING_DATA_MONGODB_URI` ao iniciar a aplicação.

## Como subir a infraestrutura

```bash
docker compose up -d
docker compose ps
```

O MongoDB possui healthcheck. O Mongo Express aguarda o banco ficar saudável antes de iniciar.

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para encerrar os contêineres preservando os dados:

```bash
docker compose down
```

Os dados ficam no volume nomeado `mongo-data`. O comando abaixo também remove esse volume e apaga os bancos locais:

```bash
docker compose down --volumes
```

### Compatibilidade do MongoDB 8 no Linux

MongoDB 8.x não inicia em hosts Linux com kernel entre 6.19 e 7.0.13 devido a uma incompatibilidade conhecida com TCMalloc. Atualizar para kernel 7.0.14 ou posterior permite manter a versão padrão do Compose.

Como alternativa temporária para desenvolvimento nesse intervalo de versões, substitua somente a variável ao iniciar os serviços:

```bash
MONGO_VERSION=7.0 docker compose up -d
```

O `compose.yaml` permanece inalterado.

## Como executar a aplicação

Com a infraestrutura ativa:

```bash
./mvnw spring-boot:run
```

A URI padrão é:

```text
mongodb://root:Mongo@localhost:27018/linguagens?authSource=admin
```

Para usar outra instância sem alterar o projeto:

```bash
SPRING_DATA_MONGODB_URI='mongodb://usuario:senha@servidor:27017/banco?authSource=admin' \
  ./mvnw spring-boot:run
```

Para carregar Java, Python, C, Rust e JavaScript no ambiente de desenvolvimento:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

A carga é idempotente: reiniciar a aplicação não duplica nem sobrescreve os registros existentes com os mesmos identificadores. As datas são aproximações didáticas quando não há uma data oficial única.

Verifique a aplicação:

```bash
curl -i http://localhost:8080/api/linguagens
```

## Endpoints

| Método | Endpoint | Entrada | Sucesso |
|---|---|---|---|
| `GET` | `/api/linguagens` | — | `200` com lista resumida |
| `GET` | `/api/linguagens/{id}` | — | `200` com representação completa |
| `POST` | `/api/linguagens` | `LinguagemCreateRequest` | `201`, `Location` e representação completa |
| `PUT` | `/api/linguagens/{id}` | `LinguagemUpdateRequest` | `200` com representação completa |
| `DELETE` | `/api/linguagens/{id}` | — | `204` sem corpo |

Consultas, atualizações e exclusões de identificadores inexistentes retornam `404`. Entradas inválidas retornam `400` com uma representação de erro consistente.

## Exemplos com curl

Listar linguagens, usando a projeção com apenas `id` e `nome`:

```bash
curl -i http://localhost:8080/api/linguagens
```

Consultar uma linguagem:

```bash
curl -i http://localhost:8080/api/linguagens/java
```

Criar uma linguagem — o corpo não possui `id`:

```bash
curl -i -X POST http://localhost:8080/api/linguagens \
  -H 'Content-Type: application/json' \
  -d '{
    "nome": "Kotlin",
    "dataCriacao": "2011-07-19",
    "autor": "JetBrains"
  }'
```

Atualizar uma linguagem — o identificador vem exclusivamente da URL:

```bash
curl -i -X PUT http://localhost:8080/api/linguagens/ID_RETORNADO \
  -H 'Content-Type: application/json' \
  -d '{
    "nome": "Kotlin",
    "dataCriacao": "2011-07-19",
    "autor": "JetBrains e comunidade"
  }'
```

Excluir uma linguagem:

```bash
curl -i -X DELETE http://localhost:8080/api/linguagens/ID_RETORNADO
```

## Como executar os testes

Testes unitários de Service e testes de Controller, sem depender de MongoDB:

```bash
./mvnw clean test
```

Somente testes unitários de Service:

```bash
./mvnw -Dtest=LinguagemServiceTest test
```

Somente testes de Controller:

```bash
./mvnw -Dtest=LinguagemControllerTest test
```

Somente o teste de integração:

```bash
./mvnw -Dtest=LinguagemApiIT test
```

Todos os testes e verificações, incluindo a integração registrada no Failsafe:

```bash
./mvnw clean verify
```

O teste de integração cria seu próprio MongoDB 7.0 com Testcontainers. Ele não depende do MongoDB instalado na máquina, do Compose de desenvolvimento, de dados anteriores ou da ordem dos testes.

## Cobertura

`verify` também gera o relatório JaCoCo e exige pelo menos 70% de cobertura de linhas no código relevante:

```bash
./mvnw clean verify
```

Abra o relatório em:

```text
target/site/jacoco/index.html
```

Na validação inicial, 81 de 84 linhas relevantes foram cobertas: **96,43%**. A classe de bootstrap e a configuração exclusiva de dados de desenvolvimento são excluídas para não distorcer a métrica pedagógica.

## Mongo Express

A interface visual está disponível em [http://localhost:18081](http://localhost:18081), com usuário `cesumar` e senha `cesumar`.

O Mongo Express foi mantido por ser simples para a demonstração em sala. A imagem oficial está descontinuada por falta de manutenção; por isso, seu uso deve ficar restrito ao ambiente didático local. Para projetos novos, ferramentas como MongoDB Compass ou DbGate são alternativas mais atuais.

## Documentação para continuidade

- `AGENTS.md`: contrato arquitetural e regras para agentes de IA;
- `HARNESS.md`: comandos operacionais e Definition of Done;
- `docs/architecture.md`: fluxo e responsabilidades das camadas;
- `docs/decisions.md`: decisões arquiteturais em pequenas ADRs;
- `docs/http-api.md`: contratos HTTP detalhados;
- `TODO.md`: evoluções deixadas deliberadamente para o futuro;
- `.agents/skills/`: procedimentos locais para CRUD, testes e revisão arquitetural.

## Problemas comuns

### Porta já utilizada

Altere `MONGO_PORT` ou `MONGO_EXPRESS_PORT` no `.env` ou apenas para um comando. Recrie os serviços e ajuste a URI da aplicação se a porta do MongoDB mudou.

### Troca de usuário ou senha não aplicada

As variáveis `MONGO_INITDB_ROOT_USERNAME` e `MONGO_INITDB_ROOT_PASSWORD` só são aplicadas ao inicializar um volume vazio. Em um ambiente descartável de estudo, remova o volume e suba os serviços novamente. Isso apaga os dados existentes:

```bash
docker compose down --volumes
docker compose up -d
```

### Mongo Express não abriu imediatamente

Na primeira execução, o Docker pode precisar baixar as imagens. Verifique:

```bash
docker compose ps
docker compose logs mongo
docker compose logs mongo-express
```
