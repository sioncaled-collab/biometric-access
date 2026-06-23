package com.biometricAccess.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biometricAccess.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Login (ya lo usas y funciona)
    Usuario findByEmailAndContrasena(String email, String contrasena);

    // 🔍 Búsqueda por identificación
    Optional<Usuario> findByIdentificacion(String identificacion);
}
