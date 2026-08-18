# MongoDB com Docker Compose

Este projeto demonstra como executar o MongoDB e uma interface visual web usando Docker Compose.

> Este ambiente foi preparado para estudo e desenvolvimento local. As credenciais são públicas e simples de propósito; não utilize esta configuração em produção.

## Documentação do projeto

- `AGENTS.md`: contrato arquitetural e regras para agentes de IA;
- `HARNESS.md`: comandos operacionais e critérios de conclusão;
- `docs/architecture.md`: fluxo e responsabilidades das camadas;
- `docs/decisions.md`: decisões arquiteturais registradas;
- `TODO.md`: evoluções deliberadamente deixadas para etapas futuras;
- `.agents/skills/`: procedimentos reutilizáveis de CRUD, testes e revisão arquitetural.

## Tecnologias utilizadas

- MongoDB 8.0
- mongo-express 1.0.2
- Docker Compose
- Java 21
- Spring Boot 3.5.16
- Maven Wrapper

O `mongo-express` facilita o primeiro contato com o banco por oferecer uma interface visual acessível pelo navegador. Sua imagem oficial está descontinuada por falta de manutenção, portanto seu uso neste projeto deve ficar restrito ao ambiente didático local. Para projetos novos ou ambientes reais, considere MongoDB Compass ou DbGate.

## Pré-requisitos

Antes de iniciar, instale:

- Docker Desktop, no Windows ou macOS; ou Docker Engine, no Linux;
- Docker Compose v2, normalmente incluído nas instalações atuais do Docker.

Confirme a instalação:

```bash
docker --version
docker compose version
```

## Arquivos da configuração

O ambiente utiliza dois arquivos:

- `compose.yaml`: descreve os serviços, a rede e o volume;
- `.env`: contém as versões, portas e credenciais locais usadas pelo Compose.

As credenciais estão fora do `compose.yaml`, mas permanecem no projeto porque não são segredos reais. Uma variável exportada no terminal tem precedência sobre o valor definido no `.env`.

## Variáveis disponíveis

O arquivo `.env` contém os seguintes valores iniciais:

```dotenv
MONGO_ROOT_USERNAME=root
MONGO_ROOT_PASSWORD=Mongo
MONGO_PORT=27018
MONGO_VERSION=8.0

MONGO_EXPRESS_USERNAME=cesumar
MONGO_EXPRESS_PASSWORD=cesumar
MONGO_EXPRESS_PORT=18081
MONGO_EXPRESS_VERSION=1.0.2-20-alpine3.19
```

Para substituir temporariamente um valor sem editar o arquivo, informe a variável antes do comando:

```bash
MONGO_ROOT_PASSWORD=OutraSenha docker compose up -d
```

No PowerShell, o equivalente é:

```powershell
$env:MONGO_ROOT_PASSWORD="OutraSenha"
docker compose up -d
```

## Iniciando o ambiente

Na raiz do projeto, execute:

```bash
docker compose up -d
```

O parâmetro `-d` mantém os contêineres em execução em segundo plano.

Verifique o estado dos serviços:

```bash
docker compose ps
```

O MongoDB possui uma verificação de saúde. O `mongo-express` só é iniciado depois que o banco responde corretamente.

## Acessando a interface web

Abra no navegador:

```text
http://localhost:18081
```

Use as credenciais da interface:

```text
Usuário: cesumar
Senha: cesumar
```

Essas credenciais protegem apenas o acesso ao `mongo-express`. O próprio MongoDB utiliza o usuário `root` e a senha `Mongo`.

## Conectando diretamente ao MongoDB

A partir da máquina local, utilize:

```text
mongodb://root:Mongo@localhost:27018/?authSource=admin
```

A porta externa é `27018`, enquanto o MongoDB continua usando a porta padrão `27017` dentro da rede Docker. Da mesma forma, o `mongo-express` é publicado na porta externa `18081`, mas utiliza `8081` dentro do contêiner. Somente as portas externas ocupam portas da máquina.

A aplicação utiliza o banco `linguagens` e, por padrão, conecta-se ao mesmo ambiente local:

```text
mongodb://root:Mongo@localhost:27018/linguagens?authSource=admin
```

Para apontar a aplicação para outra instância sem alterar o projeto, defina `SPRING_DATA_MONGODB_URI`:

```bash
SPRING_DATA_MONGODB_URI='mongodb://usuario:senha@servidor:27017/banco?authSource=admin' ./mvnw spring-boot:run
```

Não registre credenciais reais no repositório.

Para abrir o shell do MongoDB pelo próprio contêiner:

```bash
docker compose exec mongo mongosh \
  --username root \
  --password Mongo \
  --authenticationDatabase admin
```

## Persistência dos dados

Os dados são armazenados no volume nomeado `mongo-data`, montado em `/data/db`, que é o diretório oficial de dados da imagem do MongoDB.

Recriar um contêiner não apaga esse volume. Por exemplo, este comando preserva os bancos:

```bash
docker compose down
```

Para remover também os dados persistidos e começar com uma instância vazia:

```bash
docker compose down --volumes
```

> Atenção: o último comando apaga todos os bancos armazenados neste ambiente local.

## Consultando os logs

Para acompanhar todos os serviços:

```bash
docker compose logs -f
```

Para acompanhar somente o MongoDB:

```bash
docker compose logs -f mongo
```

Use `Ctrl+C` para sair da visualização sem desligar os contêineres.

## Encerrando o ambiente

Para interromper e remover os contêineres e a rede, preservando os dados:

```bash
docker compose down
```

## Detalhes importantes do Compose

- As imagens têm versões definidas, evitando mudanças inesperadas da tag `latest`.
- As portas são vinculadas a `127.0.0.1`, impedindo acesso direto por outras máquinas da rede.
- Os serviços comunicam-se pela rede interna `mongo-compose-network`.
- Dentro dessa rede, o nome `mongo` funciona como endereço do servidor de banco de dados.
- `restart: unless-stopped` reinicia os serviços após falhas, exceto quando eles forem interrompidos manualmente.
- O volume nomeado funciona da mesma forma em Windows, Linux e macOS.

## Problemas comuns

### A porta já está em uso

Altere `MONGO_PORT` ou `MONGO_EXPRESS_PORT` no `.env` e recrie os serviços:

```bash
docker compose down
docker compose up -d
```

### A troca de usuário ou senha não funcionou

As variáveis `MONGO_INITDB_ROOT_USERNAME` e `MONGO_INITDB_ROOT_PASSWORD` são aplicadas somente na primeira inicialização de um volume vazio.

Em um ambiente descartável de estudo, remova o volume e inicialize novamente:

```bash
docker compose down --volumes
docker compose up -d
```

Esse procedimento apaga os dados existentes.

### O mongo-express não abriu imediatamente

Confira o estado e os logs:

```bash
docker compose ps
docker compose logs mongo-express
```

Na primeira execução, o Docker também precisa baixar as imagens, o que pode levar alguns minutos.
