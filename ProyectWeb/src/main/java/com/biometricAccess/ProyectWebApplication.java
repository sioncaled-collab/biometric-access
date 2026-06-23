package com.biometricAccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.biometricAccess.repository")
public class ProyectWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectWebApplication.class, args);
    }
}
