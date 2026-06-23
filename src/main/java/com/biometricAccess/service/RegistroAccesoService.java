package com.biometricAccess.service;

import com.biometricAccess.model.RegistroAcceso;

import com.biometricAccess.repository.RegistroAccesoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroAccesoService {

    private final RegistroAccesoRepository registroAccesoRepository;

    public RegistroAccesoService(RegistroAccesoRepository registroAccesoRepository) {
        this.registroAccesoRepository = registroAccesoRepository;
    }



    public List<RegistroAcceso> listarPorUsuario(Long usuarioId) {
        return registroAccesoRepository.findByUsuarioId(usuarioId);
    }

    
}