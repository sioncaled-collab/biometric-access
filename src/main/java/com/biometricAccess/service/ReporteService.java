package com.biometricAccess.service;

import com.biometricAccess.dto.ReporteAccesoDTO;
import com.biometricAccess.projection.ReporteAccesoProjection;
import com.biometricAccess.repository.ReporteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public List<ReporteAccesoDTO> buscarReportes(
            String criterio,
            String valor,
            String fechaInicio,
            String fechaFin
    ) {
        if (criterio != null && criterio.equalsIgnoreCase("todos")) {
            criterio = null;
            valor = null;
        }

        List<ReporteAccesoProjection> resultados = reporteRepository.buscarReportes(
                criterio,
                valor,
                fechaInicio,
                fechaFin
        );

        return resultados.stream()
                .map(r -> new ReporteAccesoDTO(
                        r.getFecha(),
                        r.getHoraIngreso(),
                        r.getHoraSalida(),
                        r.getIdentificacion(),
                        r.getNombre(),
                        r.getApellido(),
                        r.getRol(),
                        r.getNumeroFicha(),
                        r.getTipoDocumento(),
                        r.getEquipo(),
                        r.getTipoEquipo(),
                        r.getResultado(),
                        r.getObservacion()
                ))
                .toList();
    }
}