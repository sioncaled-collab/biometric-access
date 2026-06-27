package com.biometricAccess.config;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class PythonServerStarter {

    private Process pythonProcess;

    @Bean
    public CommandLineRunner startPythonServer() {
        return args -> {

            String pythonExecutable = System.getenv().getOrDefault("PYTHON_EXECUTABLE", "python3");
            String pythonWorkdir = System.getenv().getOrDefault("PYTHON_WORKDIR", "/app/src/main/python");
            String pythonScript = System.getenv().getOrDefault("PYTHON_SCRIPT", "main.py");

            File workDir = new File(pythonWorkdir);

            if (!workDir.exists()) {
                throw new IllegalStateException("No existe la carpeta Python: " + workDir.getAbsolutePath());
            }

            File scriptFile = new File(workDir, pythonScript);

            if (!scriptFile.exists()) {
                throw new IllegalStateException("No existe el archivo Python: " + scriptFile.getAbsolutePath());
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable,
                    pythonScript
            );

            processBuilder.directory(workDir);
            processBuilder.redirectErrorStream(true);

            pythonProcess = processBuilder.start();

            System.out.println("Servidor Python iniciado correctamente.");
            System.out.println("Python executable: " + pythonExecutable);
            System.out.println("Python workdir: " + workDir.getAbsolutePath());
            System.out.println("Python script: " + scriptFile.getAbsolutePath());
        };
    }

    @PreDestroy
    public void stopPythonServer() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy();
            System.out.println("Servidor Python detenido.");
        }
    }
}
