package com.biometricAccess.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.UsuarioRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UsuarioRegistroController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioRegistroController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(
            @RequestParam String tipoDocumento,
            @RequestParam(required = false) String rol,
            @RequestParam String identificacion,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam(required = false) String numeroFicha,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam String contrasena,
            @RequestParam(required = false) MultipartFile archivo
    ) {

        try {
            identificacion = identificacion != null ? identificacion.trim() : "";
            email = email != null ? email.trim() : "";

            if (identificacion.isBlank()) {
                return ResponseEntity.badRequest().body("La identificación es obligatoria");
            }

            if (email.isBlank()) {
                return ResponseEntity.badRequest().body("El correo es obligatorio");
            }

            if (contrasena == null || contrasena.isBlank()) {
                return ResponseEntity.badRequest().body("La contraseña es obligatoria");
            }

            if (usuarioRepository.existsByIdentificacion(identificacion)) {
                return ResponseEntity.badRequest().body("La identificación ya está registrada");
            }

            if (usuarioRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body("El correo ya está registrado");
            }

            Usuario usuario = new Usuario();
            usuario.setTipoDocumento(tipoDocumento);
            usuario.setRol((rol == null || rol.isBlank()) ? "APRENDIZ" : rol.trim().toUpperCase());
            usuario.setIdentificacion(identificacion);
            usuario.setNumeroFicha(numeroFicha != null ? numeroFicha.trim() : null);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setContrasena(contrasena);

            // Guardar imagen si existe
            if (archivo != null && !archivo.isEmpty()) {
                File dir = new File("uploads");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
                String ruta = "uploads/" + nombreArchivo;

                archivo.transferTo(new File(ruta));
                usuario.setArchivo(ruta);
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuario registrado correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al guardar el archivo");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno al registrar usuario");
        }
    }
}