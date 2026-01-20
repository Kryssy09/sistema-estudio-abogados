package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "expediente_sesion_archivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedienteSesionArchivo implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idExpedienteSesionArchivo")
  private Integer idExpedienteSesionArchivo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idExpedienteSesion", nullable = false)
  private ExpedienteSesion sesion;

  @Column(name = "nombreDocumento", nullable = false, length = 250)
  private String nombreDocumento;

  @Column(name = "rutaArchivo", nullable = false, length = 1000)
  private String rutaArchivo;

  @Column(name = "estadoRegistro", nullable = false, length = 4)
  private String estadoRegistro;

  @Column(name = "idUsuarioCreador", nullable = false)
  private Integer idUsuarioCreador;

  @Column(name = "idUsuarioModificador", nullable = true)
  private Integer idUsuarioModificador;

  @Column(name = "fechaCreacion", nullable = false)
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @Column(name = "fechaModificacion", nullable = true)
  private LocalDateTime fechaModificacion;
}