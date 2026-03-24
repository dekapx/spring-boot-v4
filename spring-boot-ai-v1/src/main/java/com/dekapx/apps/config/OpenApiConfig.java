package com.dekapx.apps.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPIConfig() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Contact Service API")
                        .description("Contact Service API version 1.0"));
    }
}
