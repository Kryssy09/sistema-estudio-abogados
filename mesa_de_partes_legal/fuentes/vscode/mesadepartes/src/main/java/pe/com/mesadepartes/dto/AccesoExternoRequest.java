package pe.com.mesadepartes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccesoExternoRequest implements Serializable {

    @NotBlank(message = "El DNI es requerido")
    @Size(min = 8, max = 20, message = "El DNI debe tener entre 8 y 20 caracteres")
    private String dni;

    @NotBlank(message = "La clave de acceso es requerida")
    @Size(min = 4, max = 20, message = "La clave debe tener entre 4 y 20 caracteres")
    private String claveAcceso;

    private static final long serialVersionUID = 1L;
}