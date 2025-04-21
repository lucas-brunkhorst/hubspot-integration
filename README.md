# HubSpot Integration

Este projeto fornece uma integração simples com a API da HubSpot utilizando OAuth 2.0 para autenticação e criação de contatos. Ele inclui classes de serviço para gerenciar o OAuth, a criação de contatos e o armazenamento de tokens de autenticação. Além disso, utiliza o Feign Client para a comunicação com a API HubSpot.

## Funcionalidades

- Geração de URL de autorização OAuth
- Troca de código de autorização por token de acesso
- Criação de contatos na HubSpot
- Tratamento de erros e exceções

## Tecnologias Utilizadas

- **Spring Boot**: Framework para criação do serviço.
- **Feign Client**: Comunicação com a API da HubSpot.
- **Resilience4j**: Implementação de retry para a criação de contatos.
- **Caffeine**: Cache de tokens de autenticação.
- **JUnit**: Framework de testes.
- **Mockito**: Framework para mockar dependências em testes.

## Dependências

- Spring Boot Starter Web
- Spring Boot Starter Test
- Resilience4j Retry
- Caffeine
- Feign
- Jackson (para manipulação de JSON)
- JUnit e Mockito (para testes)

## Como Executar

1. Clone o repositório:
    ```bash
    git clone https://github.com/seu-usuario/hubspot-integration.git
    ```

2. Navegue até o diretório do projeto:
    ```bash
    cd hubspot-integration
    ```

3. Compile o projeto com Maven:
    ```bash
    mvn clean install
    ```

4. Execute a aplicação:
    ```bash
    mvn spring-boot:run
    ```

## Configuração

A aplicação usa um arquivo `application.yml` para configurar as propriedades da HubSpot. Um exemplo de configuração é:

```yaml
hubspot:
  client:
    id: seu-client-id
    secret: seu-client-secret
  redirect:
    uri: http://localhost:8080/oauth/callback
  base:
    auth:
      url: https://auth.hubspot.com/oauth/authorize
  scopes: contacts
  token:
    url: https://api.hubapi.com/oauth/v1/token
  contact:
    url: https://api.hubapi.com/contacts/v1/contact
  webhook:
    secret: seu-webhook-secret
