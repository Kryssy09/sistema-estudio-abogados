package pe.com.mesadepartes.dtos.rol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolListItem {
  private Integer id;
  private String nombreRol;
  private boolean activo;
}
