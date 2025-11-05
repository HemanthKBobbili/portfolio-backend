package com.hkb.portfolio_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI portfolioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio Backend API")
                        .description("Spring Boot REST API documentation for the Portfolio Management app.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hemanth Kumar Bobbili")
                                .email("hemanth@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
