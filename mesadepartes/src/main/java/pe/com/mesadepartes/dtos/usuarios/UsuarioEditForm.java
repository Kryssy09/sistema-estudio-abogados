package pe.com.mesadepartes.dtos.usuarios;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioEditForm {
  @NotNull
  private Integer idUsuario;

  @NotNull
  private Integer idPersona;

  @NotBlank
  private String apellidoPaterno;
  @NotBlank
  private String apellidoMaterno;
  @NotBlank
  private String nombres;

  @NotNull
  private Integer tipoDocumentoIdentidad;
  @NotBlank
  private String numeroDocumento;

  @Email
  private String correoElectronico;

  private String telefonoPersonal;
  private Integer sexo;

  private String rutaFoto;
  private Boolean removeFoto;

  @NotBlank
  @Size(max = 20)
  private String nombreUsuario;

  private String clave;

  @NotEmpty
  private List<Integer> rolesIds;
}
