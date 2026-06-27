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

            String pythonExecutable = System.getenv().getOrDefault("PYTHON_EXECUTABLE", "python");
            String pythonWorkdir = System.getenv().getOrDefault("PYTHON_WORKDIR", "src/main/python");
            String pythonScript = System.getenv().getOrDefault("PYTHON_SCRIPT", "main.py");
            String pythonHost = System.getenv().getOrDefault("PYTHON_HOST", "127.0.0.1");
            String pythonPort = System.getenv().getOrDefault("PYTHON_PORT", "8000");

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

            processBuilder.environment().put("PYTHON_HOST", pythonHost);
            processBuilder.environment().put("PYTHON_PORT", pythonPort);

            pythonProcess = processBuilder.start();

            System.out.println("Servidor Python iniciado correctamente.");
            System.out.println("Python executable: " + pythonExecutable);
            System.out.println("Python workdir: " + workDir.getAbsolutePath());
            System.out.println("Python script: " + scriptFile.getAbsolutePath());
            System.out.println("Python URL interna: http://" + pythonHost + ":" + pythonPort);
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
