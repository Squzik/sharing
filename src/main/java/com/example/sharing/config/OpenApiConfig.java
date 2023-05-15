package org.example.sharing.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Configuration;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

// Swagger http://localhost:8080/renta/swagger-ui/index.html#/

@OpenAPIDefinition(
        servers = {@Server(url = "/renta", description = "Default Server URL")},
        info = @Info(
                title = "FlatRenta API",
                version = "1.0",
                description = "Flat Renta"
        )
)
@SecurityScheme(
        name = SCHEME_NAME,
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {

    @UtilityClass
    public class SwaggerDependency {
        public static final String SCHEME_NAME = "flat_renta_config";
    }
}
