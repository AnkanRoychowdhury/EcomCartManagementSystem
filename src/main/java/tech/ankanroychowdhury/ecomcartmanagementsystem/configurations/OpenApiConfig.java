package tech.ankanroychowdhury.ecomcartmanagementsystem.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                .title("Cart Management API")
                .description("API documentation for Cart Management System")
                .version("1.0.0"));
    }
}

