package com.biometricAccess.service;

import org.springframework.stereotype.Service;

import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario validarLogin(String email, String contrasena) {
        return usuarioRepository.findByEmailAndContrasena(email, contrasena);
    }
}
