package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.Direccion;
import cl.ecomarket.ms_productos.service.DireccionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    public ResponseEntity<List<Direccion>> getAllDirecciones() {
        return ResponseEntity.ok(direccionService.getAllDirecciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Direccion> getDireccionById(@PathVariable Long id) {
        return direccionService.getDireccionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Direccion> createDireccion(@Valid @RequestBody Direccion direccion) {
        Direccion nuevaDireccion = direccionService.createDireccion(direccion);
        return new ResponseEntity<>(nuevaDireccion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Direccion> updateDireccion(@PathVariable Long id, @Valid @RequestBody Direccion direccionDetails) {
        try {
            Direccion direccionActualizada = direccionService.updateDireccion(id, direccionDetails);
            return ResponseEntity.ok(direccionActualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable Long id) {
        try {
            direccionService.deleteDireccion(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
