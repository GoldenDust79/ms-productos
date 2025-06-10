package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.UsuarioUpdateDTO; // <-- IMPORTA TU DTO AQUÍ
import cl.ecomarket.ms_productos.model.Usuario;
import cl.ecomarket.ms_productos.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService; 

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) { 
        try {
            Usuario usuarioActualizado = usuarioService.updateUsuarioFromDTO(id, usuarioUpdateDTO);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> cambiarPassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String nuevaPassword = payload.get("nuevaPassword");
            if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La nuevaPassword es requerida."));
            }
            usuarioService.cambiarPassword(id, nuevaPassword);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente para el usuario ID: " + id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id) {
        try {
            Usuario usuarioDesactivado = usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok(usuarioDesactivado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id) {
        try {
            Usuario usuarioActivado = usuarioService.activarUsuario(id);
            return ResponseEntity.ok(usuarioActivado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado con id: " + id));
        }
    }

    @PostMapping("/{usuarioId}/roles/{nombreRol}")
    public ResponseEntity<?> asignarRolAUsuario(@PathVariable Long usuarioId, @PathVariable String nombreRol) {
        try {
            Usuario usuarioConRol = usuarioService.asignarRolAUsuario(usuarioId, nombreRol.toUpperCase());
            return ResponseEntity.ok(usuarioConRol);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{usuarioId}/roles/{nombreRol}")
    public ResponseEntity<?> removerRolDeUsuario(@PathVariable Long usuarioId, @PathVariable String nombreRol) {
        try {
            Usuario usuarioSinRol = usuarioService.removerRolDeUsuario(usuarioId, nombreRol.toUpperCase());
            return ResponseEntity.ok(usuarioSinRol);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}