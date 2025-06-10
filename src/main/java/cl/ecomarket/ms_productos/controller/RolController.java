package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.service.RolService;
import jakarta.persistence.EntityNotFoundException; 
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; 

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA')")
    public ResponseEntity<?> createRol(@Valid @RequestBody Rol rol) {
        try {
            Rol nuevoRol = rolService.createRol(rol);
            return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'GERENTE_TIENDA')") 
    public ResponseEntity<List<Rol>> getAllRoles() {
        List<Rol> roles = rolService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'GERENTE_TIENDA')") 
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        Optional<Rol> rol = rolService.getRolById(id);
        return rol.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA')")
    public ResponseEntity<?> updateRol(@PathVariable Long id, @Valid @RequestBody Rol rolDetails) {
        try {
            Rol rolActualizado = rolService.updateRol(id, rolDetails);
            return ResponseEntity.ok(rolActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA')")
    public ResponseEntity<?> deleteRol(@PathVariable Long id) {
        try {
            rolService.deleteRol(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{rolId}/permisos/{nombrePermiso}") 
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA')")
    public ResponseEntity<?> asignarPermisoARol(@PathVariable Long rolId, @PathVariable String nombrePermiso) {
        try {
            Rol rolActualizado = rolService.asignarPermisoARol(rolId, nombrePermiso);
            return ResponseEntity.ok(rolActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{rolId}/permisos/{nombrePermiso}") 
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA')")
    public ResponseEntity<?> removerPermisoDeRol(@PathVariable Long rolId, @PathVariable String nombrePermiso) {
        try {
            Rol rolActualizado = rolService.removerPermisoDeRol(rolId, nombrePermiso);
            return ResponseEntity.ok(rolActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}