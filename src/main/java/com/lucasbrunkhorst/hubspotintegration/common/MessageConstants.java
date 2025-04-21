package com.lucasbrunkhorst.hubspotintegration.common;

public final class MessageConstants {

    // OAuth
    public static final String TOKEN_RENEWAL_FAILED = "Erro ao renovar token.";
    public static final String ACCESS_TOKEN_NOT_FOUND = "Não foi possível recuperar o token de acesso.";
    public static final String REFRESH_TOKEN_MISSING = "Refresh token não disponível.";
    public static final String HUBSPOT_COMMUNICATION_ERROR = "Erro ao comunicar com o HubSpot: %s";
    public static final String HUBSPOT_UNEXPECTED_ERROR = "Erro inesperado ao renovar token: %s";
    public static final String INVALID_OAUTH_CONFIGURATION = "Os parametros clientId, scopes ou redirectUri não estao configurados corretamente.";

    // Webhook
    public static final String INVALID_WEBHOOK_SIGNATURE = "Assinatura inválida";
    public static final String WEBHOOK_DESERIALIZATION_ERROR = "Erro ao desserializar o corpo do webhook: %s";
    public static final String WEBHOOK_HANDLER_NOT_FOUND = "Handler não encontrado para o evento: %s";
    public static final String EVENTS_PROCESSED_SUCCESS = "Eventos processados com sucesso";

    // Contato
    public static final String CONTACT_CREATION_FAILED = "Erro ao criar contato no HubSpot";
    public static final String HUBSPOT_CLIENT_ERROR = "Erro ao conectar com a API do HubSpot: %s";
    public static final String CONTACT_UNEXPECTED_ERROR = "Erro inesperado ao processar o contato.";
    public static final String RATE_LIMIT_EXCEEDED = "Limite de tentativas excedido";
    public static final String CONTACT_ALREADY_EXISTS = "Contato ja cadastrado";

    // Controllers
    public static final String CONTACT_CREATED_SUCCESS = "Contato criado com sucesso.";
    public static final String OAUTH_ERROR = "Erro de autenticação: ";
    public static final String HUBSPOT_API_ERROR = "Erro ao processar o contato na API do HubSpot";
    public static final String UNEXPECTED_ERROR = "Erro inesperado ao processar a solicitação.";
    public static final String AUTHORIZATION_URL_ERROR = "Erro ao gerar URL de autorização.";
    public static final String TOKEN_STORED_SUCCESS = "Token de acesso armazenado com sucesso.";
    public static final String CALLBACK_PROCESSING_ERROR = "Erro ao processar callback.";

    private MessageConstants() {
    }
}
