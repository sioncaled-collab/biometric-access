package com.biometricAccess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.biometricAccess.service.CorreoMasivoService;

@Controller


public class CorreoMasivoController {

    @Autowired
    private CorreoMasivoService correoService;

    // ✅ GET: muestra la página del formulario
    @GetMapping("/send_email")
    public String mostrarFormulario() {
        System.out.println("📩 [Controller] Cargando formulario de envío de correo masivo...");
        return "correo"; // nombre del HTML en templates/
    }

    // ✅ POST: procesa el formulario
    @PostMapping("/send_email")
    @ResponseBody
    public String enviarCorreoMasivo(
            @RequestParam("asunto") String asunto,
            @RequestParam("mensaje") String mensaje,
            @RequestParam(value = "csvFile", required = false) MultipartFile csvFile,
            @RequestParam(value = "plantilla", required = false) String plantilla) {

        System.out.println("🚀 [Controller] Entrando a enviarCorreoMasivo()");
        System.out.println("🧾 Asunto: " + asunto);
        System.out.println("🧠 Mensaje: " + (mensaje.length() > 60 ? mensaje.substring(0, 60) + "..." : mensaje));
        System.out.println("📎 Archivo CSV recibido: " + (csvFile != null ? csvFile.getOriginalFilename() : "Ninguno"));
        System.out.println("📄 Plantilla recibida: " + (plantilla != null ? "Sí" : "No"));

        try {
            correoService.enviarCorreosDesdeCSV(asunto, mensaje, plantilla, csvFile);
            System.out.println("✅ [Controller] Envío completado sin errores.");
            return "✅ Correos enviados correctamente.";
        } catch (Exception e) {
            System.out.println("❌ [Controller] Error al enviar correos: " + e.getMessage());
            e.printStackTrace();
            return "❌ Error al enviar los correos: " + e.getMessage();
        }
    }
}
