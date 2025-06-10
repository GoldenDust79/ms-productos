package cl.ecomarket.ms_productos.util;

import cl.ecomarket.ms_productos.model.*;
import cl.ecomarket.ms_productos.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ProductoRepository productoRepository;
    private final DireccionRepository direccionRepository;
    private final CarritoCompraRepository carritoCompraRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                           PermisoRepository permisoRepository, ProductoRepository productoRepository,
                           DireccionRepository direccionRepository, CarritoCompraRepository carritoCompraRepository,
                           PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository,
                           ItemCarritoRepository itemCarritoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.productoRepository = productoRepository;
        this.direccionRepository = direccionRepository;
        this.carritoCompraRepository = carritoCompraRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Ejecutando DataInitializer para cargar datos iniciales...");

        // Permisos
        Permiso pLeerProductos = crearPermisoSiNoExiste("PRODUCTOS_LEER");
        Permiso pCrearProductos = crearPermisoSiNoExiste("PRODUCTOS_CREAR");
        Permiso pEditarProductos = crearPermisoSiNoExiste("PRODUCTOS_EDITAR");
        Permiso pEliminarProductos = crearPermisoSiNoExiste("PRODUCTOS_ELIMINAR");
        Permiso pGestionarUsuarios = crearPermisoSiNoExiste("USUARIOS_GESTIONAR");
        Permiso pGestionarRoles = crearPermisoSiNoExiste("ROLES_GESTIONAR");
        Permiso pGestionarPermisos = crearPermisoSiNoExiste("PERMISOS_GESTIONAR");

        // Roles
        Rol adminRol = crearRolSiNoExiste("ADMINISTRADOR_SISTEMA", Set.of(pLeerProductos, pCrearProductos, pEditarProductos, pEliminarProductos,
                pGestionarUsuarios, pGestionarRoles, pGestionarPermisos));
        Rol gerenteRol = crearRolSiNoExiste("GERENTE_TIENDA", Set.of(pLeerProductos, pCrearProductos, pEditarProductos));
        Rol empleadoRol = crearRolSiNoExiste("EMPLEADO_VENTAS", Set.of(pLeerProductos));
        Rol logisticaRol = crearRolSiNoExiste("LOGISTICA", Set.of(pLeerProductos));

        // Usuarios
        crearUsuarioSiNoExiste("admin", "Administrador", "admin@example.com", "admin123", adminRol);
        crearUsuarioSiNoExiste("gerente", "Gerente", "gerente@example.com", "gerente123", gerenteRol);
        crearUsuarioSiNoExiste("empleado", "Empleado", "empleado@example.com", "empleado123", empleadoRol);
        crearUsuarioSiNoExiste("logistica", "Logistica", "logistica@example.com", "logistica123", logisticaRol);

        // Productos
        Producto producto1 = crearProductoSiNoExiste("PROD-001", "Producto 1", "Descripción 1", "Electronica", 100.0, 10);
        Producto producto2 = crearProductoSiNoExiste("PROD-002", "Producto 2", "Descripción 2", "Ropa", 50.0, 5);

        // Direcciones
        Usuario usuarioAdmin = usuarioRepository.findByUsername("admin").orElseThrow();
        Direccion direccion1 = crearDireccionSiNoExiste("Calle Principal", "123", "Santiago", "Santiago", "Metropolitana", "7500000", usuarioAdmin);

        // Carrito de Compras
        CarritoCompra carrito1 = crearCarritoCompraSiNoExiste(usuarioAdmin, Set.of(crearItemCarritoSiNoExiste(producto1, 2), crearItemCarritoSiNoExiste(producto2, 1)));

        // Pedidos
        Pedido pedido1 = crearPedidoSiNoExiste(LocalDateTime.now(), "COMPLETADO", 250.0, usuarioAdmin, direccion1, Set.of(
                crearDetallePedidoSiNoExiste(producto1, 1, 100.0),
                crearDetallePedidoSiNoExiste(producto2, 1, 50.0)
        ));

        log.info("DataInitializer finalizado.");
    }

    // Métodos auxiliares para crear entidades (simplificados)
    private Permiso crearPermisoSiNoExiste(String nombre) {
        return permisoRepository.findByNombre(nombre.toUpperCase()).orElseGet(() -> permisoRepository.save(new Permiso(null, nombre.toUpperCase())));
    }

    private Rol crearRolSiNoExiste(String nombre, Set<Permiso> permisos) {
        return rolRepository.findByNombre(nombre.toUpperCase().replace(" ", "_")).orElseGet(() -> rolRepository.save(new Rol(null, nombre.toUpperCase().replace(" ", "_"), permisos)));
    }

    private Usuario crearUsuarioSiNoExiste(String username, String nombreCompleto, String email, String password, Rol rol) {
        return usuarioRepository.findByUsername(username).orElseGet(() -> usuarioRepository.save(new Usuario(null, username, nombreCompleto, email, passwordEncoder.encode(password), true, Set.of(rol), new HashSet<>(), new HashSet<>(), null)));
    }

    private Producto crearProductoSiNoExiste(String codigo, String nombre, String descripcion, String categoria, Double precio, Integer stock) {
        return productoRepository.findByCodigo(codigo).orElseGet(() -> productoRepository.save(new Producto(null, codigo, nombre, descripcion, categoria, precio, stock)));
    }

    private Direccion crearDireccionSiNoExiste(String calle, String numero, String comuna, String ciudad, String region, String codigoPostal, Usuario usuario) {
        return direccionRepository.save(new Direccion(null, calle, numero, comuna, ciudad, region, codigoPostal, usuario));
    }

    private CarritoCompra crearCarritoCompraSiNoExiste(Usuario usuario, Set<ItemCarrito> items) {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setUsuario(usuario);
        carrito.setItemsCarrito(items);
        return carritoCompraRepository.save(carrito);
    }

    private ItemCarrito crearItemCarritoSiNoExiste(Producto producto, Integer cantidad) {
        ItemCarrito item = new ItemCarrito();
        item.setProducto(producto);
        item.setCantidad(cantidad);
        return itemCarritoRepository.save(item);
    }

    private Pedido crearPedidoSiNoExiste(LocalDateTime fechaPedido, String estado, Double total, Usuario usuario, Direccion direccionEnvio, Set<DetallePedido> detalles) {
        Pedido pedido = new Pedido();
        pedido.setFechaPedido(fechaPedido);
        pedido.setEstado(estado);
        pedido.setTotal(total);
        pedido.setUsuario(usuario);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setDetallesPedido(detalles);
        return pedidoRepository.save(pedido);
    }

    private DetallePedido crearDetallePedidoSiNoExiste(Producto producto, Integer cantidad, Double precioUnitario) {
        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        return detallePedidoRepository.save(detalle);
    }
}
