package pe.com.mesadepartes.dtos.area;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AreaListItem {
  private Integer id;
  private String nombreArea;
  private boolean activo;
}
