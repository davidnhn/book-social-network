package com.davidnhn.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "David",
                        email = "david.nahon@hotmail.fr",
                        url = "https://mon-portfolio.com"
                ),
                description = "OpenApi documentation for Spring security",
                title = "OpenApi specification - David",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://some-url.com"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                description = "Local ENV",
                url = "http://localhost:8088/api/v1"
                ),
                @Server(
                        description = "PROD END",
                        url = "https://environnement-de-prod.com"
                )
        },
        security = {
                @SecurityRequirement(
                        name= "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth", // le nom doit etre identique a la ligne 39
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
