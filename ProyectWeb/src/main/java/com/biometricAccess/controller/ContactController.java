package com.biometricAccess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biometricAccess.service.CorreoMasivoService;

@Controller
public class ContactController {

    @Autowired
    private CorreoMasivoService correoService;

    @PostMapping("/submit")
    public String enviarFormulario(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String message) {

        try {

            String asunto = "Nuevo mensaje desde formulario de contacto";

            String cuerpo = """
                    
                    Nuevo mensaje recibido desde el portal
                    
                    Nombre: %s
                    Correo: %s
                    Teléfono: %s
                    
                    Mensaje:
                    %s
                    
                    """.formatted(name,email,phone,message);

            // correo destino (administrador)
            String correoDestino = "adsobiometricacces@gmail.com";

            correoService.enviarCorreoIndividual(
                    correoDestino,
                    asunto,
                    cuerpo
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/gracias";
    }
}