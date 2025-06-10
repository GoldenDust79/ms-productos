package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.Direccion;
import cl.ecomarket.ms_productos.repository.DireccionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;

    public DireccionService(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    @Transactional(readOnly = true)
    public List<Direccion> getAllDirecciones() {
        return direccionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Direccion> getDireccionById(Long id) {
        return direccionRepository.findById(id);
    }

    @Transactional
    public Direccion createDireccion(Direccion direccion) {
        return direccionRepository.save(direccion);
    }

    @Transactional
    public Direccion updateDireccion(Long id, Direccion direccionDetails) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada con id: " + id));
        // Actualiza los campos de la dirección existente con los datos de direccionDetails
        // ... (implementa la lógica de actualización) ...
        return direccionRepository.save(direccion);
    }

    @Transactional
    public void deleteDireccion(Long id) {
        if (!direccionRepository.existsById(id)) {
            throw new EntityNotFoundException("Dirección no encontrada con id: " + id);
        }
        direccionRepository.deleteById(id);
    }
}
