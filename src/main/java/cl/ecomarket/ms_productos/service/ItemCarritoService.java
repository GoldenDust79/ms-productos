package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.ItemCarrito;
import cl.ecomarket.ms_productos.repository.ItemCarritoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemCarritoService {

    private final ItemCarritoRepository itemCarritoRepository;

    public ItemCarritoService(ItemCarritoRepository itemCarritoRepository) {
        this.itemCarritoRepository = itemCarritoRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemCarrito> getAllItemsCarrito() {
        return itemCarritoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ItemCarrito> getItemCarritoById(Long id) {
        return itemCarritoRepository.findById(id);
    }

    @Transactional
    public ItemCarrito createItemCarrito(ItemCarrito itemCarrito) {
        return itemCarritoRepository.save(itemCarrito);
    }

    @Transactional
    public ItemCarrito updateItemCarrito(Long id, ItemCarrito itemCarritoDetails) {
        ItemCarrito itemCarrito = itemCarritoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item de carrito no encontrado con id: " + id));
        // Actualiza los campos del item de carrito existente con los datos de itemCarritoDetails
        // ... (implementa la lógica de actualización) ...
        return itemCarritoRepository.save(itemCarrito);
    }

    @Transactional
    public void deleteItemCarrito(Long id) {
        if (!itemCarritoRepository.existsById(id)) {
            throw new EntityNotFoundException("Item de carrito no encontrado con id: " + id);
        }
        itemCarritoRepository.deleteById(id);
    }
}
