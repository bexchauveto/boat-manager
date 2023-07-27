package com.bexchauvet.boatmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Boat API", version = "1.0",
                contact = @Contact(name = "Olivier Bex-Chauvet", email = "olivier@bex-chauvet.fr",
                        url = "https://bew-chauvet.fr"),
                description = "This API is created to manage a fleet of boat all around the world"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development"),
                @Server(url = "https://bex-chauvet.fr/", description = "Production")})
public class OpenAPIConfig {

    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .description(
                                        "Provide the JWT token. JWT token can be obtained from the Login API.")
                                .bearerFormat("JWT")));

    }

}
