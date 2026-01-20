package pe.com.mesadepartes.dtos.area;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AreaCreateForm {
  @NotBlank
  private String nombreArea;
}
