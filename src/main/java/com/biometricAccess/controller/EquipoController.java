package com.biometricAccess.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biometricAccess.model.Equipo;
import com.biometricAccess.model.Usuario;
import com.biometricAccess.repository.EquipoRepository;
import com.biometricAccess.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin
public class EquipoController {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    public EquipoController(EquipoRepository equipoRepository, UsuarioRepository usuarioRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ===============================
    // LISTAR EQUIPOS POR USUARIO
    // ===============================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Equipo>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Equipo> equipos = equipoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO");
        return ResponseEntity.ok(equipos);
    }

    @GetMapping("/usuario-identificacion/{identificacion}")
    public ResponseEntity<List<Equipo>> listarPorIdentificacion(@PathVariable String identificacion) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByIdentificacion(identificacion.trim());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        List<Equipo> equipos = equipoRepository.findByUsuarioId(usuario.getId());

        return ResponseEntity.ok(equipos);
    }

    // ===============================
    // GUARDAR EQUIPO
    // ===============================
    @PostMapping
    public ResponseEntity<String> guardarEquipo(@RequestBody Equipo equipo) {

        if (equipo.getUsuarioId() == null) {
            return ResponseEntity.badRequest().body("El usuario es obligatorio");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(equipo.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        if (equipo.getMacAddress() == null || equipo.getMacAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La MAC es obligatoria");
        }

        String mac = equipo.getMacAddress().trim().toUpperCase();
        equipo.setMacAddress(mac);

        Optional<Equipo> equipoExistente = equipoRepository.findByMacAddress(mac);
        if (equipoExistente.isPresent()) {
            Equipo existente = equipoExistente.get();

            if (!existente.getUsuarioId().equals(equipo.getUsuarioId())) {
                return ResponseEntity.badRequest()
                        .body("El equipo con MAC " + mac + " ya está asociado a otro usuario");
            } else {
                return ResponseEntity.badRequest()
                        .body("El equipo con MAC " + mac + " ya está asociado a este usuario");
            }
        }

        if (equipo.getEstado() == null || equipo.getEstado().isBlank()) {
            equipo.setEstado("ACTIVO");
        }

        equipoRepository.save(equipo);
        return ResponseEntity.ok("Equipo guardado correctamente");
    }

    // ===============================
    // ELIMINAR EQUIPO
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEquipo(@PathVariable Long id) {

        Optional<Equipo> equipoOpt = equipoRepository.findById(id);

        if (equipoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("El equipo no existe");
        }

        Equipo equipo = equipoOpt.get();

        equipo.setEstado("INACTIVO");

        equipoRepository.save(equipo);

        return ResponseEntity.ok("Equipo eliminado correctamente");
    }
}