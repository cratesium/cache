package com.kafka.shikhar.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Simple health-check endpoint for load balancer / readiness probes.
 *
 * <p>This endpoint is explicitly allowed in {@link com.kafka.shikhar.app.config.SecurityConfig}
 * so no authentication token is required.
 */
@Slf4j
@RestController
@RequestMapping("/app")
public class HealthController {

    /**
     * Lightweight liveness check.
     *
     * @return 200 OK with status and server timestamp
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        log.debug("HealthController → health check called");
        return ResponseEntity.ok(Map.of(
                "status",    "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service",   "app"
        ));
    }
}
