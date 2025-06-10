package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.CarritoCompra;
import cl.ecomarket.ms_productos.service.CarritoCompraService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carritosCompra")
public class CarritoCompraController {

    private final CarritoCompraService carritoCompraService;

    public CarritoCompraController(CarritoCompraService carritoCompraService) {
        this.carritoCompraService = carritoCompraService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoCompra>> getAllCarritosCompra() {
        return ResponseEntity.ok(carritoCompraService.getAllCarritosCompra());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoCompra> getCarritoCompraById(@PathVariable Long id) {
        return carritoCompraService.getCarritoCompraById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CarritoCompra> createCarritoCompra(@Valid @RequestBody CarritoCompra carritoCompra) {
        CarritoCompra nuevoCarritoCompra = carritoCompraService.createCarritoCompra(carritoCompra);
        return new ResponseEntity<>(nuevoCarritoCompra, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoCompra> updateCarritoCompra(@PathVariable Long id, @Valid @RequestBody CarritoCompra carritoCompraDetails) {
        try {
            CarritoCompra carritoCompraActualizado = carritoCompraService.updateCarritoCompra(id, carritoCompraDetails);
            return ResponseEntity.ok(carritoCompraActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarritoCompra(@PathVariable Long id) {
        try {
            carritoCompraService.deleteCarritoCompra(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
