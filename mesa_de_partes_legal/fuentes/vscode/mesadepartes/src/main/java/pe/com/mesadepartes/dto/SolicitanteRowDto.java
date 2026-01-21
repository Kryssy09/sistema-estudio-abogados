package pe.com.mesadepartes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SolicitanteRowDto {
    private Integer id;
    private String  nombreCompleto;
    private String  dni;
    private String  correoElectronico;
    private String  telefonoPersonal;
    private String  fechaRegistro;  // yyyy-MM-dd
    private Long    expedientes;
}


