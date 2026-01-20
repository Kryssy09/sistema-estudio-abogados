package pe.com.mesadepartes.dtos.solicitante;

import lombok.Data;

@Data
public class SolicitanteOptions {
  private Integer idSolicitante;
  private String nombres;
  private String apellidoPaterno;
  private String apellidoMaterno;
  private String numeroDocumento;
}
