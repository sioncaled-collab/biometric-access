package com.biometricAccess.service;

import com.biometricAccess.model.Equipo;

import com.biometricAccess.repository.EquipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }


    public List<Equipo> listarPorUsuario(Long usuarioId) {
    return equipoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO");
    }


    public boolean eliminarEquipo(Long id) {
    Optional<Equipo> equipoOptional = equipoRepository.findById(id);

    if (equipoOptional.isEmpty()) {
        return false;
    }

    Equipo equipo = equipoOptional.get();

    equipo.setEstado("INACTIVO");

    equipoRepository.save(equipo);

    return true;
    }
}

    