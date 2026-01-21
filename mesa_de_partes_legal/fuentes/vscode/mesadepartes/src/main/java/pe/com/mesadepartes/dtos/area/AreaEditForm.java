package pe.com.mesadepartes.dtos.area;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AreaEditForm {
  @NotNull
  private Integer idArea;
  @NotBlank
  private String nombreArea;
}