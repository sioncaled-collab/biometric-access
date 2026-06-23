package com.biometricAccess.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.biometricAccess.service.CargaMasivaUsuariosService;

@RestController
@RequestMapping("/api/carga-masiva")
@CrossOrigin
public class CargaMasivaUsuariosController {

    private final CargaMasivaUsuariosService cargaMasivaUsuariosService;

    public CargaMasivaUsuariosController(CargaMasivaUsuariosService cargaMasivaUsuariosService) {
        this.cargaMasivaUsuariosService = cargaMasivaUsuariosService;
    }

    @PostMapping("/usuarios")
    public ResponseEntity<List<String>> cargarUsuarios(@RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo == null || archivo.isEmpty()) {
                return ResponseEntity.badRequest().body(List.of("Debe seleccionar un archivo CSV"));
            }

            String nombreArchivo = archivo.getOriginalFilename();
            if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body(List.of("Solo se permiten archivos CSV"));
            }

            List<String> resultados = cargaMasivaUsuariosService.procesarArchivoCSV(archivo);
            return ResponseEntity.ok(resultados);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(List.of("Error al procesar el archivo"));
        }
    }
}