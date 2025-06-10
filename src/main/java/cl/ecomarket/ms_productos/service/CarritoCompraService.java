package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.CarritoCompra;
import cl.ecomarket.ms_productos.repository.CarritoCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoCompraService {

    private final CarritoCompraRepository carritoCompraRepository;

    public CarritoCompraService(CarritoCompraRepository carritoCompraRepository) {
        this.carritoCompraRepository = carritoCompraRepository;
    }

    @Transactional(readOnly = true)
    public List<CarritoCompra> getAllCarritosCompra() {
        return carritoCompraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<CarritoCompra> getCarritoCompraById(Long id) {
        return carritoCompraRepository.findById(id);
    }

    @Transactional
    public CarritoCompra createCarritoCompra(CarritoCompra carritoCompra) {
        return carritoCompraRepository.save(carritoCompra);
    }

    @Transactional
    public CarritoCompra updateCarritoCompra(Long id, CarritoCompra carritoCompraDetails) {
        CarritoCompra carritoCompra = carritoCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrito de compra no encontrado con id: " + id));
        // Actualiza los campos del carrito de compra existente con los datos de carritoCompraDetails
        // ... (implementa la lógica de actualización) ...
        return carritoCompraRepository.save(carritoCompra);
    }

    @Transactional
    public void deleteCarritoCompra(Long id) {
        if (!carritoCompraRepository.existsById(id)) {
            throw new EntityNotFoundException("Carrito de compra no encontrado con id: " + id);
        }
        carritoCompraRepository.deleteById(id);
    }
}
