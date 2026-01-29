package com.userapi.registration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Loads the complete API specification from openapi.yaml file.
 */
@Configuration
public class OpenApiConfig {

    @Value("${openapi.spec.path}")
    private Resource openapiSpec;

    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        String content = new String(Files.readAllBytes(openapiSpec.getFile().toPath()));
        
        SwaggerParseResult result = new OpenAPIV3Parser().readContents(content);
        return result.getOpenAPI();
    }
}
