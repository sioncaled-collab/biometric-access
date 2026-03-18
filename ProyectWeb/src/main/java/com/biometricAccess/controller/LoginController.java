package com.biometricAccess.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import com.biometricAccess.model.Usuario;
import com.biometricAccess.service.UsuarioService;

@RestController
@RequestMapping("/api/login")
@CrossOrigin
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public String login(
            @RequestParam String email,
            @RequestParam String contrasena,
            HttpSession session) {

        Usuario usuario = usuarioService.validarLogin(email, contrasena);

        if (usuario != null) {

            session.setAttribute("usuarioLogeado", usuario);

            return "OK";

        } else {

            return "ERROR";

        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate(); // destruye la sesión

        return "redirect:/login";
    }
}
