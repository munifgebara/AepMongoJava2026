---
name: testing
description: Planejar, implementar e revisar testes automatizados JUnit 5 para Services, Controllers e integração MongoDB deste projeto. Usar em toda funcionalidade nova, correção de comportamento, alteração de contrato HTTP ou persistência.
---

# Testing

## Estratégia

1. Identificar o comportamento e a camada que o implementa.
2. Escolher o menor tipo de teste capaz de fornecer confiança.
3. Cobrir sucesso, falhas relevantes e limites do contrato.
4. Tornar cada teste independente, determinístico e legível.
5. Executar a suíte apropriada e depois a suíte completa.

## Testes unitários

- Executar sem Spring e sem MongoDB real.
- Priorizar regras e casos de uso do Service.
- Usar JUnit 5 e Mockito.
- Mockar dependências diretas, não o objeto sob teste.
- Verificar resultados, exceções e interações relevantes.
- Usar Arrange/Act/Assert pela organização do código, sem comentários redundantes.

## Testes de Controller

- Usar MockMvc ou mecanismo Spring equivalente.
- Isolar o Controller com o Service mockado quando o objetivo for testar a camada HTTP.
- Verificar JSON de entrada e saída, Jakarta Validation, status HTTP, `Content-Type` e tratamento centralizado de erros.
- Comprovar contratos: campos aceitos no POST/PUT, resposta completa individual e projeção resumida na listagem.
- Não duplicar nos testes de Controller regras já verificadas unitariamente no Service.

## Testes de integração

- Usar Testcontainers com uma imagem MongoDB compatível.
- Executar contra MongoDB real criado para os testes.
- Não depender de MongoDB instalado, do Compose de desenvolvimento, de dados persistidos ou da ordem de execução.
- Limpar o Repository antes de cada teste ou isolar o estado de forma equivalente.
- Cobrir inserir, recuperar, listar, atualizar e excluir.
- Preferir fluxo HTTP completo quando ele validar uma integração relevante entre Controller, Service, Repository e MongoDB.

## Reprodutibilidade

- Não compartilhar estado mutável entre testes.
- Não depender de IDs fixos gerados pelo banco.
- Não usar esperas arbitrárias.
- Manter dados de desenvolvimento fora do perfil de testes.
- Nomear testes pelo comportamento esperado.

## Execução

Usar os comandos atuais de `HARNESS.md`. Se Docker não estiver disponível, relatar claramente quais testes Testcontainers não puderam ser executados; não substituir silenciosamente o banco real por um fake.
