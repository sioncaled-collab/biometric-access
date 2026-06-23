package com.biometricAccess.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path rutaUsuarios = Paths.get("C:/Users/LENOVO/Desktop/Cosas Brayan/Programas/ProyectWebBiometricAcces/ProyectWebBiometricAcces/ProyectWebBiometricAcces/ProyectWeb/src/main/python/usuarios");

        registry.addResourceHandler("/usuarios/**")
                .addResourceLocations("file:///" + rutaUsuarios.toString().replace("\\", "/") + "/");
    }
}