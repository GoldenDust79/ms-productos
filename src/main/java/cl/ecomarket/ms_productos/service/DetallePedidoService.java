package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.DetallePedido;
import cl.ecomarket.ms_productos.repository.DetallePedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> getAllDetallesPedido() {
        return detallePedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DetallePedido> getDetallePedidoById(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Transactional
    public DetallePedido createDetallePedido(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    @Transactional
    public DetallePedido updateDetallePedido(Long id, DetallePedido detallePedidoDetails) {
        DetallePedido detallePedido = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de pedido no encontrado con id: " + id));
        // Actualiza los campos del detalle de pedido existente con los datos de detallePedidoDetails
        // ... (implementa la lógica de actualización) ...
        return detallePedidoRepository.save(detallePedido);
    }

    @Transactional
    public void deleteDetallePedido(Long id) {
        if (!detallePedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Detalle de pedido no encontrado con id: " + id);
        }
        detallePedidoRepository.deleteById(id);
    }
}
