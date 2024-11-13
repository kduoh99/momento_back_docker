package com.hackathon.momento.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private Info apiInfo() {
        return new Info()
                .version("v1.0.0")
                .title("Momento API")
                .description("Momento API 명세서");
    }

    @Bean
    public OpenAPI openAPI() {
        String authHeader = "Authorization";

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(authHeader))
                .components(new Components()
                        .addSecuritySchemes(authHeader, new SecurityScheme()
                                .name(authHeader)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")));
    }
}
