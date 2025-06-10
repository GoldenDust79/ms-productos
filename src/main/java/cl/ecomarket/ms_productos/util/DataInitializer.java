package cl.ecomarket.ms_productos.util;

import cl.ecomarket.ms_productos.model.Permiso;
import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.model.Usuario;
import cl.ecomarket.ms_productos.repository.PermisoRepository;
import cl.ecomarket.ms_productos.repository.RolRepository;
import cl.ecomarket.ms_productos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           RolRepository rolRepository,
                           PermisoRepository permisoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Ejecutando DataInitializer para cargar datos iniciales...");

        Permiso pLeerProductos = crearPermisoSiNoExiste("PRODUCTOS_LEER");
        Permiso pCrearProductos = crearPermisoSiNoExiste("PRODUCTOS_CREAR");
        Permiso pEditarProductos = crearPermisoSiNoExiste("PRODUCTOS_EDITAR");
        Permiso pEliminarProductos = crearPermisoSiNoExiste("PRODUCTOS_ELIMINAR");

        Permiso pGestionarUsuarios = crearPermisoSiNoExiste("USUARIOS_GESTIONAR");
        Permiso pGestionarRoles = crearPermisoSiNoExiste("ROLES_GESTIONAR");
        Permiso pGestionarPermisos = crearPermisoSiNoExiste("PERMISOS_GESTIONAR");

        Rol adminRol = crearRolSiNoExiste("ADMINISTRADOR_SISTEMA",
                new HashSet<>(Arrays.asList(
                        pLeerProductos, pCrearProductos, pEditarProductos, pEliminarProductos,
                        pGestionarUsuarios, pGestionarRoles, pGestionarPermisos
                )));

        Rol gerenteRol = crearRolSiNoExiste("GERENTE_TIENDA",
                new HashSet<>(Arrays.asList(
                        pLeerProductos, pCrearProductos, pEditarProductos
                )));

        Rol empleadoRol = crearRolSiNoExiste("EMPLEADO_VENTAS",
                new HashSet<>(Arrays.asList(
                        pLeerProductos
                )));

        Rol logisticaRol = crearRolSiNoExiste("LOGISTICA",
                new HashSet<>(Arrays.asList(
                        pLeerProductos
                )));

        crearUsuarioSiNoExiste("admin", "Administrador del Sistema", "admin@ecomarket.cl", "admin123", adminRol);
        crearUsuarioSiNoExiste("gerente01", "Gerente Ejemplo Uno", "gerente01@ecomarket.cl", "gerente123", gerenteRol);
        crearUsuarioSiNoExiste("empleado01", "Empleado Ejemplo Uno", "empleado01@ecomarket.cl", "empleado123", empleadoRol);
        crearUsuarioSiNoExiste("logistica01", "Logistica Ejemplo Uno", "logistica01@ecomarket.cl", "logistica123", logisticaRol);

        // Datos de prueba para Producto
        crearProductoSiNoExiste("PROD-001", "Producto 1", "Descripción del producto 1", "Electronica", 100.0, 10);
        crearProductoSiNoExiste("PROD-002", "Producto 2", "Descripción del producto 2", "Ropa", 50.0, 5);
        crearProductoSiNoExiste("PROD-003", "Producto 3", "Descripción del producto 3", "Electronica", 200.0, 2);

        log.info("DataInitializer finalizado.");
    }

    private void crearUsuarioSiNoExiste(String username, String nombreCompleto, String email, String password, Rol rol) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario user = new Usuario();
            user.setUsername(username);
            user.setNombreCompleto(nombreCompleto);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setActivo(true);
            user.setRoles(new HashSet<>(Set.of(rol)));
            usuarioRepository.save(user);
            log.info("Usuario '{}' creado.", username);
        } else {
            log.info("Usuario '{}' ya existe.", username);
        }
    }

    private Rol crearRolSiNoExiste(String nombreRol, Set<Permiso> permisos) {
        String nombreRolUpper = nombreRol.toUpperCase().replace(" ", "_");
        Optional<Rol> rolOpt = rolRepository.findByNombre(nombreRolUpper);
        if (rolOpt.isEmpty()) {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombreRolUpper);
            if (permisos != null && !permisos.isEmpty()) {
                nuevoRol.setPermisos(new HashSet<>(permisos));
            } else {
                nuevoRol.setPermisos(new HashSet<>());
            }
            rolRepository.save(nuevoRol);
            Rol rolGuardado = rolRepository.save(nuevoRol);
            log.info("Rol '{}' creado con ID: {}.", rolGuardado.getNombre(), rolGuardado.getId());
            return rolGuardado;
        }
        Rol rolExistente = rolOpt.get();
        return rolExistente;
    }

    private Permiso crearPermisoSiNoExiste(String nombrePermiso) {
        String nombrePermisoUpper = nombrePermiso.toUpperCase().replace(" ", "_");
        Optional<Permiso> permisoOpt = permisoRepository.findByNombre(nombrePermisoUpper);
        if (permisoOpt.isEmpty()) {
            Permiso nuevoPermiso = new Permiso();
            nuevoPermiso.setNombre(nombrePermisoUpper);
            permisoRepository.save(nuevoPermiso);
            log.info("Permiso '{}' creado.", nombrePermisoUpper);
            return nuevoPermiso;
        }
        return permisoOpt.get();
    }

    private void crearProductoSiNoExiste(String codigo, String nombre, String descripcion, String categoria, Double precio, Integer stock) {
        if (!usuarioRepository.existsByUsername(codigo)) {
            cl.ecomarket.ms_productos.model.Producto producto = new cl.ecomarket.ms_productos.model.Producto();
            producto.setCodigo(codigo);
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCategoria(categoria);
            producto.setPrecio(precio);
            producto.setStock(stock);
            usuarioRepository.save(producto);
            log.info("Producto '{}' creado.", codigo);
        } else {
            log.info("Producto '{}' ya existe.", codigo);
        }
    }
}
