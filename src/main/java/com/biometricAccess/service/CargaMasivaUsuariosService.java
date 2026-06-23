package com.biometricAccess.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.UsuarioRepository;

@Service
public class CargaMasivaUsuariosService {

    private final UsuarioRepository usuarioRepository;

    public CargaMasivaUsuariosService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<String> procesarArchivoCSV(MultipartFile archivo) throws IOException {
        List<String> resultados = new ArrayList<>();

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)
            );
            CSVParser csvParser = new CSVParser(
                reader,
                CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build()
            )
        ) {
            int fila = 1;

            for (CSVRecord record : csvParser) {
                fila++;

                try {
                    String tipoDocumento = record.get("tipoDocumento");
                    String rol = record.get("rol");
                    String identificacion = record.get("identificacion");
                    String nombre = record.get("nombre");
                    String apellido = record.get("apellido");
                    String email = record.get("email");
                    String telefono = record.get("telefono");
                    String contrasena = record.get("contrasena");
                    String numeroFicha = record.isMapped("numeroFicha") ? record.get("numeroFicha") : null;

                    if (identificacion == null || identificacion.trim().isEmpty()) {
                        resultados.add("Fila " + fila + ": identificación obligatoria");
                        continue;
                    }

                    if (email == null || email.trim().isEmpty()) {
                        resultados.add("Fila " + fila + ": correo obligatorio");
                        continue;
                    }

                    if (contrasena == null || contrasena.trim().isEmpty()) {
                        resultados.add("Fila " + fila + ": contraseña obligatoria");
                        continue;
                    }

                    String rolNormalizado = (rol == null || rol.isBlank())
                            ? "APRENDIZ"
                            : rol.trim().toUpperCase();

                    if ("APRENDIZ".equals(rolNormalizado)) {
                        if (numeroFicha == null || numeroFicha.trim().isEmpty()) {
                            resultados.add("Fila " + fila + ": el número de ficha es obligatorio para APRENDIZ");
                            continue;
                        }
                    }

                    if (usuarioRepository.existsByIdentificacion(identificacion.trim())) {
                        resultados.add("Fila " + fila + ": identificación ya registrada -> " + identificacion);
                        continue;
                    }

                    if (usuarioRepository.existsByEmail(email.trim())) {
                        resultados.add("Fila " + fila + ": correo ya registrado -> " + email);
                        continue;
                    }

                    Usuario usuario = new Usuario();
                    usuario.setTipoDocumento(tipoDocumento);
                    usuario.setRol(rolNormalizado);
                    usuario.setIdentificacion(identificacion.trim());
                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setEmail(email.trim());
                    usuario.setTelefono(telefono);
                    usuario.setContrasena(contrasena);
                    usuario.setNumeroFicha(
                        numeroFicha != null && !numeroFicha.trim().isEmpty()
                            ? numeroFicha.trim()
                            : null
                    );

                    usuarioRepository.save(usuario);

                    resultados.add("Fila " + fila + ": usuario registrado correctamente -> " + identificacion);

                } catch (Exception e) {
                    resultados.add("Fila " + fila + ": error procesando registro");
                }
            }
        }

        return resultados;
    }
}