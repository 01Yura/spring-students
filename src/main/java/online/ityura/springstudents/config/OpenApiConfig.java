package online.ityura.springstudents.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

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
		}
)
public class OpenApiConfig {
}


