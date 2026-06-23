package com.biometricAccess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biometricAccess.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    boolean existsByMacAddress(String macAddress);

    List<Equipo> findByUsuarioId(Long usuarioId);

    List<Equipo> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    Optional<Equipo> findByMacAddress(String macAddress);

    Optional<Equipo> findByDeviceUuid(String deviceUuid);

    boolean existsByDeviceUuid(String deviceUuid);
}