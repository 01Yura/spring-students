package online.ityura.springstudents.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring Students API",
                version = "v1",
                description = """
                        API для управления студентами.
                        Здесь описаны цели сервиса, основные сущности и примеры использования.
						Аутентификация и авторизация осуществляется через JWT токены.
						Работа с большинством эндпоинтов данного API доступна только авторизованным пользователям.
						Сначала нужно зарегистрироваться, затем залогиниться и получить токены.""",
                // termsOfService = "https://example.com/terms",
                contact = @Contact(
                        name = "Awesome QA Engineer",
                        email = "yura.primyshev@gmail.com",
                        url = "https://it-yura.online"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
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


