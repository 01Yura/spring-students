package online.ityura.springstudents.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@OpenAPIDefinition(
		info = @Info(
				title = "Spring Students API",
				version = "v1",
				description = "Развернутое описание API для управления студентами. Здесь можно описать цели сервиса, основные сущности и примеры использования.",
				// termsOfService = "https://example.com/terms",
				contact = @Contact(
						name = "Awesome QA Engineer",
						email = "yura.peimyshev@gmail.com",
						url = "https://it-yura.online"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.apache.org/licenses/LICENSE-2.0"
				)
		),
		servers = {
				@Server(url = "/", description = "Student server")
		},
		security = {
				@SecurityRequirement(name = "bearerAuth"),
				@SecurityRequirement(name = "oauth2Password")
		}
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
@SecurityScheme(
		name = "oauth2Password",
		type = SecuritySchemeType.OAUTH2,
		flows = @OAuthFlows(
				password = @OAuthFlow(
						tokenUrl = "/oauth/token"
				)
		)
)
public class OpenApiConfig {
}


