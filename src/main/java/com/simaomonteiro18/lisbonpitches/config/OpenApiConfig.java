package com.simaomonteiro18.lisbonpitches.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI lisbonPitchesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LisbonPitches API")
                        .description("API REST para pesquisa, consulta e reserva de campos de futebol na Área Metropolitana de Lisboa.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Simão Monteiro")
                                .url("https://github.com/simaomonteiro18/LisbonPitches")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

}
