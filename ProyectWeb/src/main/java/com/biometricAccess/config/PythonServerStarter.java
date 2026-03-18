package com.biometricAccess.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.io.File;

@Configuration
@Profile("!prod")
public class PythonServerStarter {

    @Bean
    CommandLineRunner startPythonServer() {
        return args -> {

            String python = "python";

            File pythonDir = new File(
                "C:/Users/LENOVO/Desktop/Cosas Brayan/Programas/ProyectWebBiometricAcces/ProyectWebBiometricAcces/ProyectWebBiometricAcces/ProyectWeb/src/main/python"
            );

            ProcessBuilder builder = new ProcessBuilder(
                    python,
                    "-m",
                    "uvicorn",
                    "main:app",
                    "--reload"
            );

            builder.directory(pythonDir);
            builder.inheritIO();
            builder.start();

            System.out.println("Servidor Python iniciado");
        };
    }
}
