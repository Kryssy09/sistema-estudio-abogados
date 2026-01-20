// pe/com/mesadepartes/dto/SolicitanteRow.java
package pe.com.mesadepartes.dto;

//import java.time.LocalDate;
import java.time.LocalDateTime;

//import pe.com.mesadepartes.entity.Perso/na;

public record ExpedienteRow(
  Integer id,
  String solicitante,
  String tipo,              // viene de tipoExpediente (ej. "CON")
  LocalDateTime fecha,      // fechaCreacion
  String estado             // estadoExpediente (ej. "Pendiente")
) {}