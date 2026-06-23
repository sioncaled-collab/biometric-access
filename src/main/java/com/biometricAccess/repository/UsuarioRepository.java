package com.biometricAccess.repository;

import com.biometricAccess.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByIdentificacion(String identificacion);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByIdentificacion(String identificacion);

    Optional<Usuario> findByEmailAndContrasena(String email, String contrasena);

    List<Usuario> findByNumeroFicha(String numeroFicha);
}