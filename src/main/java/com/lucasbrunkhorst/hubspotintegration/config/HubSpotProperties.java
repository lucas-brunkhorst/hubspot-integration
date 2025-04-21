package com.lucasbrunkhorst.hubspotintegration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "hubspot")
public class HubSpotProperties {

    private Client client;
    private Redirect redirect;
    private Base base;
    private Token token;
    private Contact contact;
    private String scopes;
    private Webhook webhook;

    @Getter
    @Setter
    public static class Client {
        private String id;
        private String secret;
    }

    @Getter
    @Setter
    public static class Redirect {
        private String uri;
    }

    @Getter
    @Setter
    public static class Base {
        private Auth auth;

        @Getter
        @Setter
        public static class Auth {
            private String url;
        }
    }

    @Getter
    @Setter
    public static class Token {
        private String url;
    }

    @Getter
    @Setter
    public static class Contact {
        private String url;
    }

    @Getter
    @Setter
    public static class Webhook {
        private String secret;
    }
}
