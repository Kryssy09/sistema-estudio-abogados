package pe.com.mesadepartes.dto;

import lombok.Data;

@Data
public class SolicitanteRequest {
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private Integer tipoDocumentoIdentidad;
    private String numeroDocumento;
    private String correoElectronico;
    private String telefonoPersonal;
    private Integer sexo;
}
