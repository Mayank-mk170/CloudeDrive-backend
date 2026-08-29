package com.cloudstorage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
// ***
    @Bean
    public OpenAPI customOpenAPI() {

        Server railwayServer = new Server()
                .url("https://cloudedrive-backend-production.up.railway.app")
                .description("Railway Production");

        return new OpenAPI()
                .info(
                        new Info()
                                .title("CloudDrive API")
                                .version("1.0")
                                .description("CloudDrive Backend API")
                )
                .servers(
                        List.of(railwayServer)
                );
    }
}