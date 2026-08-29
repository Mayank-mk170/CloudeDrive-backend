package com.cloudstorage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // ==========================================
        // RAILWAY SERVER
        // ==========================================

        Server railwayServer = new Server()
                .url("https://cloudedrive-backend-production.up.railway.app")
                .description("Railway Production");


        // ==========================================
        // JWT SECURITY SCHEME
        // ==========================================

        SecurityScheme bearerAuth =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT");


        // ==========================================
        // RETURN OPENAPI CONFIGURATION
        // ==========================================

        return new OpenAPI()

                .info(
                        new Info()
                                .title("CloudDrive API")
                                .version("1.0")
                                .description("CloudDrive Backend API")
                )

                // Railway URL
                .servers(
                        List.of(railwayServer)
                )

                // JWT definition
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        bearerAuth
                                )
                )

                // Require JWT for protected endpoints
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }
}