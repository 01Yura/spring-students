package online.ityura.springstudents.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(
				title = "Spring Students API",
				version = "v1",
				description = "API для управления студентами"
		),
		servers = {
				@Server(url = "/", description = "Default server")
		}
)
public class OpenApiConfig {
}


