package com.serenia.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures the OpenAPI specification exposed by the platform.
 */
@Configuration
public class OpenApiConfiguration {
    // Properties
    @Value("${spring.application.name}")
    String applicationName;

    @Value("${documentation.application.description}")
    String applicationDescription;

    @Value("${documentation.application.version}")
    String applicationVersion;

    private static final String REPOSITORY_URL =
            "https://github.com/upc-pre-202620-1acc0238-4945-vitalcare/serenia-platform";

    private static final String API_GUIDE = """

            ### Bounded contexts
            - **Identity and Access Management:** users, profile photo and sessions.
            - **Care Circle:** care circles, family links, invitation codes, care shifts and shared notes.
            - **Daily Check-in:** daily check-ins and their preferences.
            - **Social Companionship:** audio messages, photo messages and social reminders.
            - **Wellbeing Monitoring:** small wins and wellbeing suggestions.
            - **Alerts and Safety:** emergency alerts and inactivity alerts.

            ### How to try it
            1. Register a user with `POST /api/v1/users`. The role is `OLDER_ADULT` or `DISTANT_RELATIVE`.
            2. Sign in with `POST /api/v1/sessions` and copy the token from the response.
            3. Press **Authorize**, paste the token (without the `Bearer` prefix) and confirm.

            Every endpoint requires the token except the registration, the sign-in and this documentation.

            ### Errors and language
            Errors are returned as JSON with a `code`, a `message` and, sometimes, `details`.
            Messages are translated with the `Accept-Language` header (`en` by default, also `es`).
            All instants are expressed in UTC.
            """;

    // Methods

    /**
     * Builds the OpenAPI document used by Swagger UI and client generation tools.
     *
     * @return configured OpenAPI descriptor
     */
    @Bean
    public OpenAPI learningPlatformOpenApi() {

        // General configuration
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription + "\n" + API_GUIDE)
                        .version(this.applicationVersion))
                .externalDocs(new ExternalDocumentation()
                        .description("Serenia Platform repository and README (VitalCare team)")
                        .url(REPOSITORY_URL));

        // Use a relative server URL so Swagger UI always targets the same origin
        // it is being served from (localhost, Docker or Azure), avoiding CORS issues.
        openApi.servers(List.of(
                new Server()
                        .url("/")
                        .description("Current environment")
        ));

        // Add a security scheme
      final String securitySchemeName = "bearerAuth";

      openApi.addSecurityItem(new SecurityRequirement()
                      .addList(securitySchemeName))
              .components(new Components()
                      .addSecuritySchemes(securitySchemeName,
                              new SecurityScheme()
                                      .name(securitySchemeName)
                                      .type(SecurityScheme.Type.HTTP)
                                      .scheme("bearer")
                                      .bearerFormat("JWT")
                                      .description("JWT obtained from POST /api/v1/sessions. Paste only the token, "
                                              + "Swagger UI adds the Bearer prefix.")));

        return openApi;
    }
}