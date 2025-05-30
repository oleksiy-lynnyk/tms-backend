package org.example.tmsstriker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TMS Striker API")
                        .version("1.0.0")
                        .description("API для управління тест-сьютами та прогоном тестів")
                );
    }
}
