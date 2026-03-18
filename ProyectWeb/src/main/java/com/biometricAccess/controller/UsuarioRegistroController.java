package com.biometricAccess.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.UsuarioRepository;

@RestController
@RequestMapping("/api")
public class UsuarioRegistroController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioRegistroController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam String tipoDocumento,
            @RequestParam String rol,
            @RequestParam String identificacion,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            @RequestParam(required = false) MultipartFile archivo
    ) {

        try {
            Usuario usuario = new Usuario();
            usuario.setTipoDocumento(tipoDocumento);
            usuario.setRol(rol);
            usuario.setIdentificacion(identificacion);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setContrasena(contrasena);

            // Guardar imagen si existe
            if (archivo != null && !archivo.isEmpty()) {
                File dir = new File("uploads");
                if (!dir.exists()) dir.mkdirs();

                String ruta = "uploads/" + archivo.getOriginalFilename();
                archivo.transferTo(new File(ruta));
                usuario.setArchivo(ruta);
            }

            usuarioRepository.save(usuario);
            return "OK";

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
