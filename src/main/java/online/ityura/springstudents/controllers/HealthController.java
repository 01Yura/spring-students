package online.ityura.springstudents.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import online.ityura.springstudents.dto.health.HealthResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@Hidden
@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ResponseEntity<HealthResponse> health() {
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        String uptimeHms = formatUptime(uptimeMs);
        HealthResponse body = new HealthResponse("UP", uptimeHms, Instant.now().toString());
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore().mustRevalidate())
                .body(body);
    }

    private String formatUptime(long uptimeMs) {
        long totalSeconds = uptimeMs / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

