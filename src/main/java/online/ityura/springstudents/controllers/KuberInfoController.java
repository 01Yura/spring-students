package online.ityura.springstudents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import online.ityura.springstudents.dto.kuberinfo.KuberInfoResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "system", description = "Системные эндпоинты для мониторинга и диагностики")
public class KuberInfoController {

    @GetMapping("/kuberinfo")
    @Operation(
            summary = "Информация о Kubernetes окружении",
            description = "Возвращает информацию о контейнере, поде, ноде и операционной системе, " +
                    "на которых запущено приложение. Полезно для диагностики и понимания, " +
                    "на какой под и воркер-ноду попал запрос при использовании балансировщика нагрузки (доступен без авторизации)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация успешно получена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KuberInfoResponse.class),
                            examples = @ExampleObject(name = "kuberInfoResponse",
                                    value = """
                                    {
                                      "podName": "spring-students-app-7d8f9c4b5-abc12",
                                      "nodeName": "worker-node-1",
                                      "osName": "Linux",
                                      "osVersion": "5.10.0-18-cloud-amd64",
                                      "osArch": "amd64",
                                      "hostname": "spring-students-app-7d8f9c4b5-abc12"
                                    }
                                    """)))
    })
    public ResponseEntity<KuberInfoResponse> kuberinfo() {
        // Получаем имя пода из переменной окружения HOSTNAME (автоматически устанавливается Kubernetes)
        String podName = System.getenv("HOSTNAME");
        if (podName == null || podName.isEmpty()) {
            podName = "unknown";
        }

        // Получаем имя ноды из переменной окружения NODE_NAME (если настроено через downward API)
        String nodeName = System.getenv("NODE_NAME");
        if (nodeName == null || nodeName.isEmpty()) {
            nodeName = "not configured";
        }

        // Получаем информацию об операционной системе
        String osName = System.getProperty("os.name", "unknown");
        String osVersion = System.getProperty("os.version", "unknown");
        String osArch = System.getProperty("os.arch", "unknown");

        // Получаем hostname через InetAddress
        String hostname = "unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Если не удалось получить, используем значение по умолчанию
        }

        KuberInfoResponse response = new KuberInfoResponse(
                podName,
                nodeName,
                osName,
                osVersion,
                osArch,
                hostname
        );

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore().mustRevalidate())
                .body(response);
    }
}

