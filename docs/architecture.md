# Arquitetura

O projeto utiliza uma arquitetura em camadas simples para manter responsabilidades visíveis aos estudantes.

## Fluxo de entrada

```text
HTTP
  |
Controller
  |
Request DTO
  |
Service
  |
Repository
  |
MongoDB
```

O `Controller` recebe a requisição, valida o `Request DTO` e delega o caso de uso ao `Service`. O `Service` coordena regras e persistência. O `Repository` é a fronteira de acesso ao MongoDB.

## Fluxo de saída

```text
MongoDB
  |
Model
  |
Service
  |
Mapper
  |
Response DTO
  |
Controller
  |
JSON
```

O MongoDB devolve um `Model` de persistência. O `Service` seleciona o resultado do caso de uso e o `Mapper` o converte em um `Response DTO`. O `Controller` transforma esse contrato em JSON e atribui o status HTTP adequado.

## Responsabilidades

- **Controller:** protocolo HTTP, validação da entrada e códigos de resposta.
- **Request DTO:** contrato imutável de um caso de uso de entrada.
- **Response DTO:** formato público devolvido ao cliente.
- **Mapper:** conversão explícita, sem acesso ao banco ou regra de negócio.
- **Service:** casos de uso e regras de negócio.
- **Repository:** persistência e consultas.
- **Model:** estrutura persistida no MongoDB.
- **Exception:** falhas de domínio e representação consistente de erros HTTP.
- **Configuration:** integração com infraestrutura e recursos exclusivos de ambientes específicos.

Nenhum `Model` anotado com `@Document` deve atravessar diretamente a fronteira HTTP.
