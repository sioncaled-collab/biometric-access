package com.biometricAccess.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biometricAccess.model.Usuario;
import com.biometricAccess.service.UsuarioService;

@Controller
public class LoginViewController {

    private final UsuarioService usuarioService;

    public LoginViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String contrasena,
            HttpSession session
    ) {
        Usuario usuario = usuarioService.validarLogin(email, contrasena);

        if (usuario == null) {
            return "redirect:/login?error";
        }

        session.setAttribute("usuarioLogeado", usuario);

        String rol = usuario.getRol() != null ? usuario.getRol().trim().toUpperCase() : "";

        switch (rol) {
            case "ADMINISTRADOR":
                return "redirect:/admin";

            case "COORDINADOR":
                return "redirect:/reportes2";

            case "INSTRUCTOR":
                return "redirect:/reportes2";

            case "GUARDA_SEGURIDAD":
                return "redirect:/biometrico";

            case "APRENDIZ":
                return "redirect:/perfil";

            default:
                session.invalidate();
                return "redirect:/login?rolerror";
        }
    }
}