package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.model.Usuario;
import cl.ecomarket.ms_productos.model.UsuarioUpdateDTO;
import cl.ecomarket.ms_productos.repository.RolRepository;
import cl.ecomarket.ms_productos.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class); 


    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          @Lazy PasswordEncoder passwordEncoder) { 
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es requerido.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido.");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida.");
        }

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + usuario.getUsername());
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + usuario.getEmail());
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword().trim()));
        if (usuario.getRoles() == null) {
            usuario.setRoles(new HashSet<>());
        }
        if (usuario.isActivo() == false) { 
             usuario.setActivo(true);
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

        if (usuarioDetails.getNombreCompleto() != null) {
            usuario.setNombreCompleto(usuarioDetails.getNombreCompleto());
        }

        if (usuarioDetails.getEmail() != null && !usuario.getEmail().equals(usuarioDetails.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDetails.getEmail())) {
                throw new IllegalArgumentException("El nuevo email ya está registrado: " + usuarioDetails.getEmail());
            }
            usuario.setEmail(usuarioDetails.getEmail());
        }

        if (usuarioDetails.isActivo() != false) { 
            usuario.setActivo(usuarioDetails.isActivo());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cambiarPassword(Long usuarioId, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + usuarioId));
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        usuario.setPassword(passwordEncoder.encode(nuevaPassword.trim()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public Usuario asignarRolAUsuario(Long usuarioId, String nombreRol) {
        log.info("--- ASIGNAR ROL INICIO ---");
        log.info("Petición para asignar rol: '{}' al usuario ID: {}", nombreRol, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + usuarioId));
        log.info("Usuario encontrado: {} (ID: {})", usuario.getUsername(), usuario.getId());

        Rol rolAAgregar = rolRepository.findByNombre(nombreRol.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con nombre: " + nombreRol));
        log.info("Rol a agregar (buscado por nombre '{}'): {} (ID: {})", nombreRol.toUpperCase(), rolAAgregar.getNombre(), rolAAgregar.getId());

        if (usuario.getRoles() == null) {
            log.info("Inicializando colección de roles para el usuario.");
            usuario.setRoles(new HashSet<>());
        }

        log.info("Roles actuales del usuario '{}' ANTES de la verificación:", usuario.getUsername());
        if (usuario.getRoles().isEmpty()) {
            log.info("  (El usuario no tiene roles actualmente)");
        } else {
            for (Rol r : usuario.getRoles()) {
                log.info("  - Rol existente: {} (ID: {})", r.getNombre(), r.getId());
            }
        }

        boolean yaTieneElRol = false;
        if (!usuario.getRoles().isEmpty()) {
            for (Rol rolExistente : usuario.getRoles()) {
                if (rolExistente.getId().equals(rolAAgregar.getId())) {
                    yaTieneElRol = true;
                    log.info("VERIFICACIÓN: El usuario YA TIENE el rol '{}' (ID: {}) que se intenta agregar.", rolAAgregar.getNombre(), rolAAgregar.getId());
                    break;
                }
            }
        }
        if (!yaTieneElRol) {
            log.info("VERIFICACIÓN: El usuario NO tiene el rol '{}' (ID: {}) que se intenta agregar.", rolAAgregar.getNombre(), rolAAgregar.getId());
        }

        if (yaTieneElRol) {
            log.warn("PREVENCIÓN: El usuario ID {} ya tiene asignado el rol: {}. Lanzando IllegalArgumentException.", usuario.getId(), rolAAgregar.getNombre());
            throw new IllegalArgumentException("El usuario ID " + usuario.getId() + " ya tiene asignado el rol: " + rolAAgregar.getNombre());
        }

        log.info("PROCEDIENDO A AÑADIR: Añadiendo rol '{}' (ID: {}) a la colección de roles del usuario '{}'", rolAAgregar.getNombre(), rolAAgregar.getId(), usuario.getUsername());
        usuario.getRoles().add(rolAAgregar); 

        log.info("Roles del usuario '{}' DESPUÉS de añadir a la colección (ANTES DE SAVE):", usuario.getUsername());
        for (Rol r : usuario.getRoles()) {
            log.info("  - Rol en colección: {} (ID: {})", r.getNombre(), r.getId());
        }

        Usuario usuarioGuardado = null;
        try {
            log.info("Intentando llamar a usuarioRepository.save(usuario)...");
            usuarioGuardado = usuarioRepository.save(usuario);
            log.info("usuarioRepository.save(usuario) completado exitosamente.");
        } catch (DataIntegrityViolationException e) {
            log.error("ERROR DataIntegrityViolationException durante save: {}", e.getMessage(), e);
            throw e; 
        } catch (Exception e) {
            log.error("ERROR Inesperado durante save: {}", e.getMessage(), e);
            throw e; 
        }

        log.info("Roles del usuario '{}' DESPUÉS de guardar en BD:", usuarioGuardado.getUsername());
        if (usuarioGuardado.getRoles().isEmpty()) {
            log.info("  (El usuario no tiene roles después de guardar)");
        } else {
            for (Rol r : usuarioGuardado.getRoles()) {
                log.info("  - Rol guardado: {} (ID: {})", r.getNombre(), r.getId());
            }
        }
        log.info("--- ASIGNAR ROL FIN ---");
        return usuarioGuardado;
    }

    @Transactional
    public Usuario removerRolDeUsuario(Long usuarioId, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + usuarioId));
        Rol rol = rolRepository.findByNombre(nombreRol.toUpperCase()) 
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con nombre: " + nombreRol));

        if (usuario.getRoles() != null) {
            usuario.getRoles().remove(rol);
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateUsuarioFromDTO(Long id, UsuarioUpdateDTO dto) { 
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de actualizar usuario no encontrado con ID: {}", id);
                    return new EntityNotFoundException("Usuario no encontrado con id: " + id);
                });

        log.info("Actualizando usuario ID: {} con DTO: {}", id, dto);

        boolean modificado = false;

        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().trim().isEmpty() && !dto.getNombreCompleto().equals(usuario.getNombreCompleto())) {
            log.debug("Actualizando nombreCompleto para usuario ID: {} de '{}' a '{}'", id, usuario.getNombreCompleto(), dto.getNombreCompleto());
            usuario.setNombreCompleto(dto.getNombreCompleto().trim());
            modificado = true;
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty() && !usuario.getEmail().equalsIgnoreCase(dto.getEmail().trim())) {
            String nuevoEmail = dto.getEmail().trim();
            if (usuarioRepository.existsByEmail(nuevoEmail)) {
                log.warn("Intento de actualizar a email existente: {} para usuario ID: {}", nuevoEmail, id);
                throw new IllegalArgumentException("El nuevo email '" + nuevoEmail + "' ya está registrado por otro usuario.");
            }
            log.debug("Actualizando email para usuario ID: {} de '{}' a '{}'", id, usuario.getEmail(), nuevoEmail);
            usuario.setEmail(nuevoEmail);
            modificado = true;
        }

        if (dto.getActivo() != null && dto.getActivo() != usuario.isActivo()) { 
            log.debug("Actualizando estado 'activo' para usuario ID: {} de '{}' a '{}'", id, usuario.isActivo(), dto.getActivo());
            usuario.setActivo(dto.getActivo());
            modificado = true;
        }


        if (modificado) {
            log.info("Guardando cambios para usuario ID: {}", id);
            return usuarioRepository.save(usuario);
        } else {
            log.info("No se detectaron cambios para el usuario ID: {}. No se realiza el guardado.", id);
            return usuario; 
        }
    }

    
}