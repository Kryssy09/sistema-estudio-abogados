package pe.com.mesadepartes.dtos.rol;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolCreateForm {
  @NotBlank
  private String nombreRol;
}
