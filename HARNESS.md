# Harness operacional

Este arquivo documenta como validar e operar o projeto. Ele complementa, mas não substitui, o contrato de desenvolvimento em `AGENTS.md`.

## Pré-requisitos

- Java 21
- Maven ou Maven Wrapper
- Docker
- Docker Compose v2

Confira o ambiente:

```bash
java -version
mvn -version
docker --version
docker compose version
```

O Maven Wrapper acompanha o repositório. Prefira `./mvnw` para usar a mesma versão do Maven em todas as máquinas.

## Comandos importantes

Compilar sem executar testes:

```bash
./mvnw clean compile -DskipTests
```

Executar testes unitários e de Controller:

```bash
./mvnw clean test
```

Executar somente os testes unitários de Service:

```bash
./mvnw -Dtest=LinguagemServiceTest test
```

Executar somente os testes de integração:

```bash
./mvnw -Dtest=LinguagemApiIT test
```

Executar todos os testes, incluindo a integração registrada no Failsafe, e gerar o relatório JaCoCo:

```bash
./mvnw clean verify
```

O relatório fica em `target/site/jacoco/index.html`, e o build falha se o código relevante ficar abaixo de 70% de cobertura de linhas.

Iniciar a aplicação:

```bash
./mvnw spring-boot:run
```

Subir a infraestrutura existente:

```bash
docker compose up -d
```

Verificar os contêineres:

```bash
docker compose ps
```

Parar os contêineres preservando os dados:

```bash
docker compose down
```

Verificar a aplicação depois de iniciada:

```bash
curl -i http://localhost:8080/
curl -i http://localhost:8080/crud.html
curl -i http://localhost:8080/api/linguagens
curl -i http://localhost:8080/v3/api-docs
```

A página inicial fica em `http://localhost:8080/`, o cliente CRUD em `http://localhost:8080/crud.html` e a documentação visual em `http://localhost:8080/docs`.

## Testes de integração

Os testes Testcontainers precisam do daemon Docker disponível. Eles criam seu próprio MongoDB e não dependem do `compose.yaml` nem dos dados locais.

O contêiner de integração e o Compose de desenvolvimento usam `mongo:7.0`. A versão foi escolhida porque a imagem `mongo:8.0` recusa iniciar em alguns hosts com kernel Linux 6.19 ou mais novo. A versão do Compose pode ser substituída pontualmente, por exemplo com `MONGO_VERSION=8.0 docker compose up -d`, em uma máquina compatível.

## Definition of Done

Uma tarefa somente está concluída quando:

1. o projeto compila;
2. todos os testes aplicáveis passam;
3. testes relevantes foram adicionados ou atualizados;
4. não existem erros de compilação;
5. não existem imports desnecessários;
6. a API preserva os contratos existentes;
7. a documentação relevante continua correta;
8. limitações do ambiente ou validações não executadas foram relatadas explicitamente.
