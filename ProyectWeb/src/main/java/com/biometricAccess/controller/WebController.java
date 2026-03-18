package com.biometricAccess.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "BiometrixAccess";
    }

    @GetMapping("/biometric")
    public String biometric() {
        return "BiometrixAccess";
    }

    @GetMapping("/sobre-nosotros")
    public String sobrenosotros() {
        return "sobre-nosotros";
    }

    @GetMapping("/servicios")
    public String servicios() {
        return "servicios";
    }

    @GetMapping("/formulario-de-contacto")
    public String formulariodecontacto() {
        return "formulario-de-contacto";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/gestion_de_usuario")
    public String gestiondeusario(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "gestion_de_usuario";
    }

    @GetMapping("/dar_baja_usuario")
    public String dardebaja(HttpSession session) {

        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "dar_baja_usuario";
    }

    @GetMapping("/interfaceinterna")
    public String interfaceinterna() {
        return "interfaceinterna";
    }

    @GetMapping("/Modificar_Usuario")
    public String modificarusuario(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "Modificar_Usuario";
    }

    @GetMapping("/plataformadeadministracion")
    public String plataformadeadministracion() {
        return "plataformadeadministracion";
    }

    @GetMapping("/registro")
    public String registro(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "registro";
    }

    @GetMapping("/correo")
    public String correo(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "correo";
    }

    @GetMapping("/reportes")
    public String reportes(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "reportes";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate(); // destruye la sesión

        return "redirect:/login";
    }
    
    @GetMapping("/biometrico")
    public String biometrico(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "biometrico";
    }
    @GetMapping("/capturar_foto")
    public String capturar_foto(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "capturar_foto";
    }
    
    @GetMapping("/gracias")
    public String gracias() {
        return "gracias";
    }





}
