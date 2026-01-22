package pe.com.mesadepartes.dtos.rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolEditForm {
  @NotNull
  private Integer idRol;
  @NotBlank
  private String nombreRol;
}
