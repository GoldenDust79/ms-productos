package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.Permiso;
import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.repository.PermisoRepository;
import cl.ecomarket.ms_productos.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
//import java.util.Set;

@Service
public class RolService {

    private static final Logger log = LoggerFactory.getLogger(RolService.class);

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository; // Necesario si vas a asignar permisos a roles

    public RolService(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @Transactional
    public Rol createRol(Rol rol) {
        // Normalizar el nombre del rol (ej: a mayúsculas y con guiones bajos)
        String nombreNormalizado = rol.getNombre().toUpperCase().replace(" ", "_");
        if (rolRepository.existsByNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("El rol con el nombre '" + nombreNormalizado + "' ya existe.");
        }
        rol.setNombre(nombreNormalizado);

        // Si los permisos vienen en el request y quieres persistirlos (más avanzado)
        // Set<Permiso> permisosPersistidos = new HashSet<>();
        // if (rol.getPermisos() != null) {
        //     for (Permiso permisoReq : rol.getPermisos()) {
        //         // Asumir que los permisos se envían por ID o nombre y se buscan en la BD
        //         Permiso p = permisoRepository.findByNombre(permisoReq.getNombre())
        //                          .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado: " + permisoReq.getNombre()));
        //         permisosPersistidos.add(p);
        //     }
        // }
        // rol.setPermisos(permisosPersistidos);
        // Por ahora, crearemos el rol sin manejar la asignación de permisos desde aquí,
        // eso se puede hacer en un endpoint separado o al actualizar.
        if (rol.getPermisos() == null) {
            rol.setPermisos(new HashSet<>());
        }

        log.info("Creando nuevo rol: {}", rol.getNombre());
        return rolRepository.save(rol);
    }

    @Transactional(readOnly = true)
    public List<Rol> getAllRoles() {
        log.info("Obteniendo todos los roles");
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRolById(Long id) {
        log.info("Buscando rol con ID: {}", id);
        return rolRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRolByNombre(String nombre) {
        log.info("Buscando rol con nombre: {}", nombre);
        return rolRepository.findByNombre(nombre.toUpperCase().replace(" ", "_"));
    }

    @Transactional
    public Rol updateRol(Long id, Rol rolDetails) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));

        String nombreNormalizado = rolDetails.getNombre().toUpperCase().replace(" ", "_");
        // Verificar si el nuevo nombre ya existe en OTRO rol
        if (!rolExistente.getNombre().equals(nombreNormalizado) && rolRepository.existsByNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe otro rol con el nombre: " + nombreNormalizado);
        }
        rolExistente.setNombre(nombreNormalizado);

        // Lógica para actualizar permisos si es necesario (similar a la de createRol)
        // rolExistente.setPermisos(...);

        log.info("Actualizando rol ID {}: nuevo nombre {}", id, rolExistente.getNombre());
        return rolRepository.save(rolExistente);
    }

    @Transactional
    public void deleteRol(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado con ID: " + id);
        }
        // Considerar la lógica si este rol está asignado a usuarios.
        // ¿Se impide la eliminación? ¿Se desasignan los usuarios?
        // Por ahora, simplemente eliminamos.
        log.info("Eliminando rol con ID: {}", id);
        rolRepository.deleteById(id);
    }

    // --- Métodos para gestionar permisos de un rol (Ejemplos) ---

    @Transactional
    public Rol asignarPermisoARol(Long rolId, String nombrePermiso) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + rolId));
        Permiso permiso = permisoRepository.findByNombre(nombrePermiso.toUpperCase().replace(" ", "_"))
                .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado con nombre: " + nombrePermiso));

        if (rol.getPermisos().contains(permiso)) {
            log.info("El rol '{}' ya tiene el permiso '{}'", rol.getNombre(), permiso.getNombre());
            return rol; // O lanzar excepción
        }

        rol.getPermisos().add(permiso);
        log.info("Asignando permiso '{}' al rol '{}'", permiso.getNombre(), rol.getNombre());
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol removerPermisoDeRol(Long rolId, String nombrePermiso) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + rolId));
        Permiso permiso = permisoRepository.findByNombre(nombrePermiso.toUpperCase().replace(" ", "_"))
                .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado con nombre: " + nombrePermiso));

        if (!rol.getPermisos().contains(permiso)) {
            log.info("El rol '{}' NO tiene el permiso '{}' para remover", rol.getNombre(), permiso.getNombre());
            return rol; // O lanzar excepción
        }

        rol.getPermisos().remove(permiso);
        log.info("Removiendo permiso '{}' del rol '{}'", permiso.getNombre(), rol.getNombre());
        return rolRepository.save(rol);
    }
}