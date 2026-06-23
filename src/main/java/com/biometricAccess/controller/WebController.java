package com.biometricAccess.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.biometricAccess.model.Equipo;
import com.biometricAccess.model.RegistroAcceso;
import com.biometricAccess.model.Usuario;

import com.biometricAccess.service.RegistroAccesoService;
import com.biometricAccess.service.EquipoService;

import org.springframework.ui.Model;
import java.util.List;

@Controller
public class WebController {

    private final RegistroAccesoService registroAccesoService;
    private final EquipoService equipoService;

    public WebController(RegistroAccesoService registroAccesoService, EquipoService equipoService) {
        this.registroAccesoService = registroAccesoService;
        this.equipoService = equipoService;
    }

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

    @GetMapping("/reportes2")
    public String reportes2(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "reportes2";
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


    @GetMapping("/gestion_equipos")
    public String geston_equipos(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "gestion_equipos";
    }

    @GetMapping("/carga_masiva_usuarios")
    public String cargaMasivaUsuarios(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "carga_masiva_usuarios";
    }

    @GetMapping("/admin")
    public String admin(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "gestion_de_usuario";
    }

    @GetMapping("/biometrico_kiosko")
    public String biometricoKiosko(HttpSession session) {
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }
        return "biometrico_kiosko";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuarioLogeado");

        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        // mantener datos personales
        model.addAttribute("usuario", usuarioLogeado);

        // menú lateral según rol
        model.addAttribute("esAdministrador", "Administrador".equalsIgnoreCase(usuarioLogeado.getRol()));
        model.addAttribute("esCoordinador", "Coordinador".equalsIgnoreCase(usuarioLogeado.getRol()));
        model.addAttribute("esInstructor", "Instructor".equalsIgnoreCase(usuarioLogeado.getRol()));
        model.addAttribute("esGuarda", "Guarda".equalsIgnoreCase(usuarioLogeado.getRol()));
        model.addAttribute("esAprendiz", "Aprendiz".equalsIgnoreCase(usuarioLogeado.getRol()));

        // consultas
        List<RegistroAcceso> registros = registroAccesoService.listarPorUsuario(usuarioLogeado.getId());
        List<Equipo> dispositivos = equipoService.listarPorUsuario(usuarioLogeado.getId());

        model.addAttribute("registrosAcceso", registros);
        model.addAttribute("dispositivosAsociados", dispositivos);

        return "perfil";
    }

}
