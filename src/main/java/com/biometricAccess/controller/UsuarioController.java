package com.biometricAccess.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ===============================
    // ✅ CREAR USUARIO
    // ===============================
    @PostMapping
    public ResponseEntity<String> guardarUsuario(
            @RequestParam String tipoDocumento,
            @RequestParam(required = false) String rol,
            @RequestParam String identificacion,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam(required = false) String numeroFicha,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam String contrasena,
            @RequestParam(required = false) MultipartFile archivo) throws IOException {

        identificacion = identificacion != null ? identificacion.trim() : "";
        email = email != null ? email.trim() : "";

        if (identificacion.isBlank()) {
            return ResponseEntity.badRequest().body("La identificación es obligatoria");
        }

        if (email.isBlank()) {
            return ResponseEntity.badRequest().body("El email es obligatorio");
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
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setNumeroFicha(numeroFicha != null ? numeroFicha.trim() : null);

        if (contrasena == null || contrasena.isBlank()) {
            return ResponseEntity.badRequest().body("La contraseña es obligatoria");
        }

        usuario.setContrasena(contrasena);

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
    }

    

    // ===============================
    // 🔍 BUSCAR USUARIO
    // ===============================
    @GetMapping("/{identificacion}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable String identificacion) {
        String id = identificacion.trim();

        Optional<Usuario> usuario = usuarioRepository.findByIdentificacion(id);

        return usuario
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===============================
    // 🔍 BUSCAR USUARIOS POR NÚMERO DE FICHA
    // ===============================

    @GetMapping("/ficha/{numeroFicha}")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorFicha(@PathVariable String numeroFicha) {
        List<Usuario> usuarios = usuarioRepository.findByNumeroFicha(numeroFicha.trim());

        if (usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    // ===============================
    // ❌ DAR DE BAJA (ELIMINAR)
    // ===============================
    @DeleteMapping("/{identificacion}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String identificacion) {
        String id = identificacion.trim();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByIdentificacion(id);

        if (usuarioOpt.isPresent()) {
            usuarioRepository.delete(usuarioOpt.get());
            return ResponseEntity.ok("Usuario eliminado correctamente");
        }

        return ResponseEntity.notFound().build();
    }

    // ===============================
    // 📋 LISTAR TODOS LOS USUARIOS
    // ===============================
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}