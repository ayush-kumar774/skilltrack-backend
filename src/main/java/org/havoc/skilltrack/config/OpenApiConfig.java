package org.havoc.skilltrack.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SkillTrack API")
                        .version("v1.0")
                        .description("Developer productivity tracker backend 🚀")
                        .contact(new Contact()
                                .name("Aayush Kumar")
                                .email("kumarayushsingh774@gmail.com")
                                .url("https://github.com/ayush-kumar774")));

    }
}

