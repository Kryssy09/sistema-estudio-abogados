package pe.com.mesadepartes.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expediente_sesion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedienteSesion implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idExpedienteSesion")
  private Integer idExpedienteSesion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idExpediente", nullable = false)
  private Expediente expediente;

  @Column(name = "secuencia", nullable = false)
  private Integer secuencia;

  @Column(name = "fechaSesion", nullable = false)
  private LocalDateTime fechaSesion;

  @Column(name = "estadoSesion", nullable = false, length = 6)
  private String estadoSesion;

  @Column(name = "detallesSesion", nullable = false, length = 4000)
  private String detallesSesion;

  @Column(name = "resolucionSesion", nullable = true)
  private Integer resolucionSesion;

  @Column(name = "rutaArchivoActa", nullable = true, length = 1000)
  private String rutaArchivoActa;

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
