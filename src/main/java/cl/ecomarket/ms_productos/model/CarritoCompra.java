package cl.ecomarket.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carritos_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @NotNull(message = "El carrito debe estar asociado a un usuario.")
    private Usuario usuario;

    @OneToMany(mappedBy = "carritoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemCarrito> itemsCarrito = new HashSet<>();
}
