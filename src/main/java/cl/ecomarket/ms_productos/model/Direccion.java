package cl.ecomarket.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La calle no puede estar vacía.")
    @Size(max = 255, message = "La calle no puede exceder los 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String calle;

    @NotBlank(message = "El número no puede estar vacío.")
    @Size(max = 50, message = "El número no puede exceder los 50 caracteres.")
    @Column(nullable = false, length = 50)
    private String numero;

    @Size(max = 255, message = "La comuna no puede exceder los 255 caracteres.")
    @Column(length = 255)
    private String comuna;

    @NotBlank(message = "La ciudad no puede estar vacía.")
    @Size(max = 255, message = "La ciudad no puede exceder los 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String ciudad;

    @NotBlank(message = "La región no puede estar vacía.")
    @Size(max = 255, message = "La región no puede exceder los 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String region;

    @Size(max = 20, message = "El código postal no puede exceder los 20 caracteres.")
    @Column(length = 20)
    private String codigoPostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
