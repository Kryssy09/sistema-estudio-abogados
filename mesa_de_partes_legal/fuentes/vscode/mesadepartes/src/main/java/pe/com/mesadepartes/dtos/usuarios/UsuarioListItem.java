package pe.com.mesadepartes.dtos.usuarios;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioListItem {
  private Integer id;
  private String apellidoPaterno;
  private String apellidoMaterno;
  private String nombres;
  private String username;
  private boolean activo;
  private java.util.List<String> roles;
}
