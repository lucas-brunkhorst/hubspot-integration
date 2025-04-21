package com.lucasbrunkhorst.hubspotintegration.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API Meetime", version = "v1", description = "Integração com HubSpot"))
public class OpenApiConfig {
}
