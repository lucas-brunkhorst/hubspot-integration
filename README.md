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

## Requisitos

- **Java 17+**
- **Maven** para gerenciamento de dependências
- **ngrok** para criar um túnel de redirecionamento para o HubSpot durante o processo de OAuth
- **HubSpot Developer Account** para obter as credenciais necessárias

## Como usar o ngrok para OAuth com HubSpot

Durante o processo de OAuth, o HubSpot precisa de um redirecionamento de volta para um URL específico após a autenticação. Para que isso funcione localmente, é necessário usar uma ferramenta como o **ngrok** para criar um túnel que redireciona a URL de desenvolvimento local para um endereço público acessível pela HubSpot.

### Passos:

1. Instale o ngrok:

   - **Windows**: Baixe o ngrok para Windows [aqui](https://ngrok.com/download) e siga as instruções de instalação.

   - **macOS**:
     ```bash
     brew install ngrok
     ```

   - **Linux**:
     ```bash
     sudo snap install ngrok
     ```

2. Após a instalação, inicie o túnel:
   ```bash
   ngrok http 8080


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
    id: <SEU_CLIENT_ID>
    secret: <SEU_CLIENT_SECRET>
  redirect:
    uri: http://<URL_DO_NGROK>/oauth/callback  # Substitua pela URL fornecida pelo ngrok
  base:
    auth:
      url: https://auth.hubspot.com/oauth/authorize
  scopes: contacts
  token:
    url: https://api.hubapi.com/oauth/v1/token
  contact:
    url: https://api.hubapi.com/contacts/v1/contact
  webhook:
    secret: <SEU_WEBHOOK_SECRET>
```
## Melhorias Futuras

- **Uso de TestContainers**: Para testar a integração com o HubSpot de forma mais isolada e controlada, podemos utilizar o **TestContainers** para criar ambientes de testes temporários e facilitar a integração com bancos de dados, APIs e outras dependências externas.
  
- **Uso de WebFlux**: Para tornar a aplicação mais reativa e não-bloqueante, podemos explorar a introdução do **Spring WebFlux**, permitindo o processamento de requisições de forma assíncrona e escalável.

- **Melhorias na Segurança**: Implementar um fluxo completo de verificação de segurança utilizando JWT para a comunicação entre microserviços e proteger ainda mais os dados durante o processo de autenticação e comunicação com a API do HubSpot.

- **Monitoramento e Logging**: Implementar uma solução de monitoramento utilizando Spring Actuator, Prometheus ou Grafana para ter visibilidade sobre a performance da aplicação, e melhorar a parte de logging utilizando ELK (Elasticsearch, Logstash, Kibana) ou outros serviços de logging centralizado.

