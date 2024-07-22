package com.testecaju.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI configOpenApi(){
        return new OpenAPI().info(new Info().description("Implementação do autorizador proposto.")
                .title("Desafio Técnico Caju")
                .version("1.0"));
    }

}