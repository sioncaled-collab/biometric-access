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
    public String guardarUsuario(
            @RequestParam String tipoDocumento,
            @RequestParam String rol,
            @RequestParam String identificacion,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            @RequestParam(required = false) MultipartFile archivo
    ) throws IOException {

        Usuario usuario = new Usuario();
        usuario.setTipoDocumento(tipoDocumento);
        usuario.setRol(rol);
        usuario.setIdentificacion(identificacion.trim());
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);

        if (contrasena != null && !contrasena.isBlank()) {
            usuario.setContrasena(contrasena);
        }

        if (archivo != null && !archivo.isEmpty()) {
            File dir = new File("uploads");
            if (!dir.exists()) dir.mkdirs();

            String ruta = "uploads/" + archivo.getOriginalFilename();
            archivo.transferTo(new File(ruta));
            usuario.setArchivo(ruta);
        }

        usuarioRepository.save(usuario);

        return "OK";
    }

    // ===============================
    // 🔍 BUSCAR USUARIO
    // ===============================
    @GetMapping("/{identificacion}")
    public ResponseEntity<Usuario> buscarUsuario(
            @PathVariable String identificacion
    ) {
        String id = identificacion.trim();

        System.out.println("🔍 Buscando usuario: [" + id + "]");

        Optional<Usuario> usuario =
                usuarioRepository.findByIdentificacion(id);

        return usuario
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // ===============================
    // ❌ DAR DE BAJA (ELIMINAR)
    // ===============================
@DeleteMapping("/{identificacion}")
public ResponseEntity<Void> eliminarUsuario(
        @PathVariable String identificacion
) {
    String id = identificacion.trim();

    Optional<Usuario> usuarioOpt = usuarioRepository.findByIdentificacion(id);

    if (usuarioOpt.isPresent()) {
        usuarioRepository.delete(usuarioOpt.get());
        return ResponseEntity.ok().build();
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
