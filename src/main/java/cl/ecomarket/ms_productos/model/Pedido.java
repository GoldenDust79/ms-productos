package cl.ecomarket.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha del pedido no puede ser nula.")
    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @NotBlank(message = "El estado del pedido no puede estar vacío.")
    @Column(nullable = false, length = 50)
    private String estado; // Ej: PENDIENTE, COMPLETADO, CANCELADO, EN_ENVIO

    @NotNull(message = "El total del pedido no puede ser nulo.")
    @Min(value = 0, message = "El total del pedido no puede ser negativo.")
    @Column(nullable = false)
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetallePedido> detallesPedido = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_envio_id")
    private Direccion direccionEnvio; // Dirección específica para este pedido
}
