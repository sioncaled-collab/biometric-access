package com.biometricAccess.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biometricAccess.dto.AccesoRequest;
import com.biometricAccess.model.Equipo;
import com.biometricAccess.model.RegistroAcceso;
import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.EquipoRepository;
import com.biometricAccess.repository.RegistroAccesoRepository;
import com.biometricAccess.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/accesos")
@CrossOrigin
public class AccesoController {

    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final RegistroAccesoRepository registroAccesoRepository;

    public AccesoController(
            UsuarioRepository usuarioRepository,
            EquipoRepository equipoRepository,
            RegistroAccesoRepository registroAccesoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.registroAccesoRepository = registroAccesoRepository;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarAcceso(@RequestBody AccesoRequest request) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByIdentificacion(request.getIdentificacion());

            if (usuarioOpt.isEmpty()) {
                respuesta.put("ok", false);
                respuesta.put("mensaje", "Usuario no encontrado");
                return ResponseEntity.badRequest().body(respuesta);
            }

            Usuario usuario = usuarioOpt.get();

            if (request.getEquipoId() == null) {
                respuesta.put("ok", false);
                respuesta.put("mensaje", "Debe seleccionar un equipo");
                return ResponseEntity.badRequest().body(respuesta);
            }

            Optional<Equipo> equipoOpt = equipoRepository.findById(request.getEquipoId());

            if (equipoOpt.isEmpty()) {
                respuesta.put("ok", false);
                respuesta.put("mensaje", "Equipo no encontrado");
                return ResponseEntity.badRequest().body(respuesta);
            }

            Equipo equipo = equipoOpt.get();

            if (!equipo.getUsuarioId().equals(usuario.getId())) {
                RegistroAcceso registro = new RegistroAcceso();
                registro.setUsuarioId(usuario.getId());
                registro.setEquipoId(equipo.getId());
                registro.setResultado("DENEGADO");
                registro.setObservacion("Equipo no pertenece al usuario");
                registroAccesoRepository.save(registro);

                respuesta.put("ok", false);
                respuesta.put("mensaje", "El equipo seleccionado no pertenece al usuario");
                return ResponseEntity.badRequest().body(respuesta);
            }

            RegistroAcceso registro = new RegistroAcceso();
            registro.setUsuarioId(usuario.getId());
            registro.setEquipoId(equipo.getId());
            registro.setResultado("APROBADO");
            registro.setObservacion("Ingreso biométrico aprobado");
            registroAccesoRepository.save(registro);

            respuesta.put("ok", true);
            respuesta.put("mensaje", "Acceso registrado correctamente");
            respuesta.put("usuario", usuario.getNombre() + " " + usuario.getApellido());
            respuesta.put("equipo", equipo.getNombreEquipo());
            respuesta.put("tipoEquipo", equipo.getTipoEquipo());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("ok", false);
            respuesta.put("mensaje", "Error interno: " + e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }
}
