package online.ityura.springstudents.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import online.ityura.springstudents.dto.kuberinfo.KuberInfoResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Hidden
@RestController
public class KuberInfoController {

    @GetMapping("/kuberinfo")
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

