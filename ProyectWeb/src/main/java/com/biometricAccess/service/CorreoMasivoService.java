package com.biometricAccess.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CorreoMasivoService {

    @Autowired
    private JavaMailSender mailSender;



    /**
     * Envía correos a todos los destinatarios listados en el CSV.
     * Si hay plantilla HTML, se usa en lugar del texto simple.
     */
    public void enviarCorreosDesdeCSV(String asunto, String mensaje, String plantilla, MultipartFile csvFile) throws Exception {
        System.out.println("🚀 [CorreoMasivoService] Iniciando envío masivo de correos...");
        System.out.println("📌 Asunto recibido: " + asunto);
        System.out.println("📝 Mensaje base recibido: " + (mensaje != null ? mensaje.substring(0, Math.min(mensaje.length(), 80)) + "..." : "Nulo"));
        System.out.println("📄 Plantilla: " + (plantilla != null && !plantilla.isBlank() ? "Sí se recibió plantilla HTML" : "No se recibió plantilla"));
        System.out.println("📁 Archivo CSV: " + (csvFile != null ? csvFile.getOriginalFilename() : "Nulo"));

        if (csvFile == null || csvFile.isEmpty()) {
            System.out.println("❌ Error: No se adjuntó ningún archivo CSV.");
            throw new IllegalArgumentException("Debe adjuntar un archivo CSV con los correos electrónicos.");
        }

        List<String> destinatarios = leerCorreosDesdeCSV(csvFile);
        System.out.println("✅ Total de destinatarios leídos: " + destinatarios.size());

        if (destinatarios.isEmpty()) {
            System.out.println("⚠️ El archivo CSV no contiene correos válidos en la columna 'email'.");
            throw new IllegalArgumentException("El archivo CSV no contiene correos válidos en la columna 'email'.");
        }

        int contador = 1;
        for (String correo : destinatarios) {
            System.out.println("➡️ Enviando correo " + contador + " de " + destinatarios.size() + " a: " + correo);
            try {
                enviarCorreoIndividual(correo, asunto, 
                    (plantilla != null && !plantilla.isBlank()) ? plantilla : mensaje);
                System.out.println("✅ Correo enviado correctamente a: " + correo);
            } catch (Exception e) {
                System.out.println("❌ Error enviando correo a " + correo + ": " + e.getMessage());
            }
            contador++;
        }

        System.out.println("🎯 [CorreoMasivoService] Proceso de envío masivo finalizado.");
    }

    /**
     * Lee los correos electrónicos desde un archivo CSV.
     * Se espera una columna con encabezado "email".
     */
    private List<String> leerCorreosDesdeCSV(MultipartFile csvFile) throws IOException {
        System.out.println("📥 [CorreoMasivoService] Leyendo correos desde el archivo CSV...");

        List<String> correos = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = new CSVParser(reader, 
                 CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreEmptyLines(true))
        ) {
            for (CSVRecord record : parser) {
                String correo = record.get("email"); // ✅ El encabezado debe llamarse "email"
                if (correo != null && !correo.isBlank()) {
                    correos.add(correo.trim());
                    System.out.println("📧 Correo encontrado: " + correo.trim());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: No se encontró la columna 'email' en el CSV.");
            throw new IllegalArgumentException("El archivo CSV debe tener una columna llamada 'email'.");
        }

        System.out.println("📦 Total de correos extraídos: " + correos.size());
        return correos;
    }

    /**
     * Envía un correo individual a un destinatario.
     * Admite contenido HTML.
     */
    public void enviarCorreoIndividual(String destinatario, String asunto, String contenido) throws Exception {
        System.out.println("📨 [CorreoMasivoService] Preparando mensaje para: " + destinatario);

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(contenido, true); // true => interpreta HTML

        System.out.println("📬 Enviando mensaje...");
        mailSender.send(mensaje);
        System.out.println("✅ Correo enviado exitosamente a: " + destinatario);
    }
}
