package com.biometricAccess.config;

import com.biometricAccess.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void agregarDatosSesion(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");

        if (usuario != null) {
            String rol = usuario.getRol() != null ? usuario.getRol().trim().toUpperCase() : "";

            model.addAttribute("usuarioSesion", usuario);
            model.addAttribute("rolSesion", rol);

            model.addAttribute("esAdministrador", "ADMINISTRADOR".equals(rol));
            model.addAttribute("esCoordinador", "COORDINADOR".equals(rol));
            model.addAttribute("esInstructor", "INSTRUCTOR".equals(rol));
            model.addAttribute("esGuarda", "GUARDA_SEGURIDAD".equals(rol));
            model.addAttribute("esAprendiz", "APRENDIZ".equals(rol));
        }
    }
}