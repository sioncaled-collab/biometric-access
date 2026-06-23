package com.biometricAccess.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FaceAccessValidator {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/scripts/validar_acceso.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Acceso concedido desde Java");
            } else {
                System.out.println("🚫 Acceso denegado desde Java");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
