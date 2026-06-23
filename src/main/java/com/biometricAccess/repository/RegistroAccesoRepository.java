package com.biometricAccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biometricAccess.model.RegistroAcceso;

import java.util.List;

public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {
    
    List<RegistroAcceso> findByUsuarioId(Long usuarioId);
}