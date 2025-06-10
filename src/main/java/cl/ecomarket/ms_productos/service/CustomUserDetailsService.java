package cl.ecomarket.ms_productos.service; // <-- ASEGÚRATE QUE ESTE PAQUETE SEA EL CORRECTO PARA TU PROYECTO

import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.model.Usuario;
import cl.ecomarket.ms_productos.repository.UsuarioRepository;
import org.slf4j.Logger; // <-- IMPORTACIÓN NECESARIA
import org.slf4j.LoggerFactory; // <-- IMPORTACIÓN NECESARIA
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.authentication.DisabledException; // Si decides usar esta excepción
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    // Añade el logger
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.info("Intentando cargar usuario por username/email: {}", usernameOrEmail);

        // Intenta buscar por username primero, luego por email si no se encuentra
        Usuario usuario = usuarioRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> usuarioRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> {
                            log.warn("Usuario no encontrado con username o email: {}", usernameOrEmail);
                            return new UsernameNotFoundException("Usuario no encontrado con username o email: " + usernameOrEmail);
                        }));

        log.info("Usuario encontrado en BD: {} (ID: {}), Activo: {}", usuario.getUsername(), usuario.getId(), usuario.isActivo());

        if (!usuario.isActivo()) {
            log.warn("La cuenta del usuario {} (ID: {}) está desactivada.", usuario.getUsername(), usuario.getId());
            // Spring Security considera DisabledException como una AuthenticationException que puede ser manejada específicamente
            // pero UsernameNotFoundException también funciona para negar el acceso.
            // throw new org.springframework.security.authentication.DisabledException("La cuenta del usuario está desactivada: " + usernameOrEmail);
            throw new UsernameNotFoundException("La cuenta del usuario está desactivada: " + usernameOrEmail);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            log.warn("Usuario {} (ID: {}) no tiene roles asignados.", usuario.getUsername(), usuario.getId());
        } else {
            log.info("Procesando roles para el usuario {} (ID: {}):", usuario.getUsername(), usuario.getId());
            for (Rol rol : usuario.getRoles()) {
                // Asumimos que rol.getNombre() devuelve el nombre del rol como está en la BD (ej: "ADMINISTRADOR_SISTEMA")
                // Spring Security por defecto antepone "ROLE_" a las cadenas de hasRole().
                String authorityName = "ROLE_" + rol.getNombre().toUpperCase().replace(" ", "_");
                authorities.add(new SimpleGrantedAuthority(authorityName));
                log.info("  Rol de BD: '{}', Autoridad añadida para Spring Security: '{}'", rol.getNombre(), authorityName);
            }
        }

        if (authorities.isEmpty()) {
            // Esto es una advertencia crítica. Si un usuario no tiene roles, no podrá acceder a nada protegido.
            log.warn("¡ADVERTENCIA! El usuario {} (ID: {}) no terminó con ninguna autoridad/rol asignado para Spring Security. No podrá acceder a recursos protegidos por roles.", usuario.getUsername(), usuario.getId());
        } else {
            log.info("Autoridades finales asignadas a {}: {}", usuario.getUsername(), authorities);
        }

        return new org.springframework.security.core.userdetails.User(
                // El primer argumento es el "username" que Spring Security usa internamente.
                // Puede ser el email, el username del usuario, o lo que prefieras,
                // siempre que sea único y lo uses consistentemente.
                usuario.getEmail(), // Usamos el email como el "username" para UserDetails
                usuario.getPassword(), // Esta debe ser la contraseña HASHEADA de la BD
                usuario.isActivo(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}