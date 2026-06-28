package com.biometricAccess.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/biometrico")
public class BiometricoProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String pythonBaseUrl =
            System.getenv().getOrDefault("PYTHON_INTERNAL_URL", "http://127.0.0.1:8000");

    @PostMapping("/validar_acceso")
    public ResponseEntity<String> validarAcceso(@RequestBody Map<String, Object> body) {

        String url = pythonBaseUrl + "/validar_acceso";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @PostMapping("/guardar_rostro")
    public ResponseEntity<String> guardarRostro(@RequestBody Map<String, Object> body) {

        String url = pythonBaseUrl + "/guardar_rostro";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        String url = pythonBaseUrl + "/";
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }
}
