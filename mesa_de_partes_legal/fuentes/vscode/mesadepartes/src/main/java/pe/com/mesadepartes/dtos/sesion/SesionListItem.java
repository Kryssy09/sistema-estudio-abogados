package pe.com.mesadepartes.dtos.sesion;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SesionListItem {
  private Integer idExpedienteSesion;
  private String estadoSesion;
  private LocalDateTime fechaSesion;
  private Integer resolucionSesion;
  private String detallesSesion;
  private Integer secuencia;
}
