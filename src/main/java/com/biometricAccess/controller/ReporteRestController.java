package com.biometricAccess.controller;

import com.biometricAccess.dto.ReporteAccesoDTO;
import com.biometricAccess.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteRestController {

    private final ReporteService reporteService;

    public ReporteRestController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/accesos")
    public ResponseEntity<List<ReporteAccesoDTO>> obtenerReportes(
            @RequestParam(required = false) String criterio,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        List<ReporteAccesoDTO> reportes = reporteService.buscarReportes(
                criterio,
                valor,
                fechaInicio,
                fechaFin
        );

        return ResponseEntity.ok(reportes);
    }
}