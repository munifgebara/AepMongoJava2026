# Contratos HTTP

Base path: `/api/linguagens`.

| Método | Caminho | Entrada | Resposta de sucesso | Recurso ausente |
|---|---|---|---|---|
| `GET` | `/api/linguagens` | — | `200` com lista de `LinguagemSummaryResponse` | — |
| `GET` | `/api/linguagens/{id}` | — | `200` com `LinguagemResponse` | `404` |
| `POST` | `/api/linguagens` | `LinguagemCreateRequest` | `201`, cabeçalho `Location` e `LinguagemResponse` | — |
| `PUT` | `/api/linguagens/{id}` | `LinguagemUpdateRequest` | `200` com `LinguagemResponse` | `404` |
| `DELETE` | `/api/linguagens/{id}` | — | `204` sem corpo | `404` |

## Criação e atualização

POST e PUT recebem contratos independentes com a mesma forma inicial:

```json
{
  "nome": "Java",
  "dataCriacao": "1995-05-23",
  "autor": "James Gosling"
}
```

O campo `id` não pertence ao corpo. Na atualização, ele é obtido exclusivamente do path.

### Validação

Nos contratos de criação e atualização:

- `nome` é obrigatório e não aceita texto formado somente por espaços;
- `autor` é obrigatório e não aceita texto formado somente por espaços;
- `dataCriacao` é obrigatória e deve usar o formato ISO `yyyy-MM-dd`.

Violações retornam `400 Bad Request`. Não há restrição artificial sobre o período histórico da linguagem.

## Listagem resumida

```json
[
  {
    "id": "123",
    "nome": "Java"
  }
]
```

## Consulta individual

```json
{
  "id": "123",
  "nome": "Java",
  "dataCriacao": "1995-05-23",
  "autor": "James Gosling"
}
```

Erros de validação e recursos ausentes usam a representação tipada e consistente definida pelo tratamento global de exceções.
