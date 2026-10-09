package com.vittig.spring_digital_wallet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(){
        OpenAPI openApi = new OpenAPI();
        Info info = new Info();
        info.setTitle("Spring Digital Wallet API");
        info.setDescription("REST API for authentication, wallet management and transfers");
        info.setVersion("1.0");
        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setBearerFormat("JWT");
        securityScheme.setType(SecurityScheme.Type.HTTP);
        securityScheme.scheme("bearer");
        securityScheme.name("bearerAuth");

        Components components = new Components();
        components.setSecuritySchemes(Map.of("bearerAuth", securityScheme));

        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("bearerAuth");


        openApi.setInfo(info);
        openApi.components(components);
        openApi.setSecurity(List.of(securityRequirement));
        return openApi;
    }
}
