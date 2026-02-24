package com.capgemini.chat_history.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RAG Chat History API",
                version = "1.0",
                description = """
            API for managing RAG chat history and sessions
            ### Rate Limiting Policy
            - **Limit:** 50 requests per minute per API Key.
            - **Exceeded:** Returns `429 Too Many Requests`.
            - **Header:** Use `X-API-KEY` for authentication.
            """
        )
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                                .name("X-API-KEY")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Enter your secret API key here")));
    }
}
