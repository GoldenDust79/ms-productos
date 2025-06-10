package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.ItemCarrito;
import cl.ecomarket.ms_productos.service.ItemCarritoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/itemsCarrito")
public class ItemCarritoController {

    private final ItemCarritoService itemCarritoService;

    public ItemCarritoController(ItemCarritoService itemCarritoService) {
        this.itemCarritoService = itemCarritoService;
    }

    @GetMapping
    public ResponseEntity<List<ItemCarrito>> getAllItemsCarrito() {
        return ResponseEntity.ok(itemCarritoService.getAllItemsCarrito());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCarrito> getItemCarritoById(@PathVariable Long id) {
        return itemCarritoService.getItemCarritoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemCarrito> createItemCarrito(@Valid @RequestBody ItemCarrito itemCarrito) {
        ItemCarrito nuevoItemCarrito = itemCarritoService.createItemCarrito(itemCarrito);
        return new ResponseEntity<>(nuevoItemCarrito, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemCarrito> updateItemCarrito(@PathVariable Long id, @Valid @RequestBody ItemCarrito itemCarritoDetails) {
        try {
            ItemCarrito itemCarritoActualizado = itemCarritoService.updateItemCarrito(id, itemCarritoDetails);
            return ResponseEntity.ok(itemCarritoActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemCarrito(@PathVariable Long id) {
        try {
            itemCarritoService.deleteItemCarrito(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
