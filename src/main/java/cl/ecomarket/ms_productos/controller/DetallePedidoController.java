package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.DetallePedido;
import cl.ecomarket.ms_productos.service.DetallePedidoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detallesPedido")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    public DetallePedidoController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public ResponseEntity<List<DetallePedido>> getAllDetallesPedido() {
        return ResponseEntity.ok(detallePedidoService.getAllDetallesPedido());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> getDetallePedidoById(@PathVariable Long id) {
        return detallePedidoService.getDetallePedidoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetallePedido> createDetallePedido(@Valid @RequestBody DetallePedido detallePedido) {
        DetallePedido nuevoDetallePedido = detallePedidoService.createDetallePedido(detallePedido);
        return new ResponseEntity<>(nuevoDetallePedido, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> updateDetallePedido(@PathVariable Long id, @Valid @RequestBody DetallePedido detallePedidoDetails) {
        try {
            DetallePedido detallePedidoActualizado = detallePedidoService.updateDetallePedido(id, detallePedidoDetails);
            return ResponseEntity.ok(detallePedidoActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetallePedido(@PathVariable Long id) {
        try {
            detallePedidoService.deleteDetallePedido(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
