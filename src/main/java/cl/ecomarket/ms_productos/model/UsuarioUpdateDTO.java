package cl.ecomarket.ms_productos.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioUpdateDTO {
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    private String nombreCompleto;

    @Email(message = "Debe ser una dirección de correo electrónico válida")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    private Boolean activo; 
}