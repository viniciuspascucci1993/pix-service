package com.pixservice.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pixServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("💸 Pix Service API")
                        .description("API simulando operações de carteira digital e transferências Pix com idempotência.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vinícius Torres Pascucci")
                                .email("vinicius.pascucci1@gmail.com")
                                .url("https://www.linkedin.com/in/vinicius-pascucci-5a4024151/"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/viniciuspascucci1993"));
    }
}
