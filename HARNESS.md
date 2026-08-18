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

Executar todos os testes:

```bash
./mvnw clean test
```

Executar somente os testes unitários e de Controller, excluindo testes nomeados com o sufixo `IT`:

```bash
./mvnw test -Dtest='!*IT'
```

Executar somente os testes de integração:

```bash
./mvnw test -Dtest='*IT'
```

Executar verificações e gerar o relatório JaCoCo:

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
curl -i http://localhost:8080/api/linguagens
```

## Testes de integração

Os testes Testcontainers precisam do daemon Docker disponível. Eles criam seu próprio MongoDB e não dependem do `compose.yaml` nem dos dados locais.

O contêiner de integração usa `mongo:7.0`. MongoDB 8.x não inicia em hosts com kernel Linux entre 6.19 e 7.0.13 por uma incompatibilidade conhecida com TCMalloc; o Compose de desenvolvimento permanece inalterado e volta a funcionar nesse host após atualização para kernel 7.0.14 ou posterior.

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
