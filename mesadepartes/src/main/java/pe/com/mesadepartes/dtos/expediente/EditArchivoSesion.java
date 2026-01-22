package pe.com.mesadepartes.dtos.expediente;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditArchivoSesion {
  private Integer idExpedienteSesion;
  private String estadoSesion;
  private String detallesSesion;
  private Integer resolucionSesion;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime fechaSesion;
}
