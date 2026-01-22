package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "area")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Area implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idArea")
  private Integer idArea;

  @Column(name = "nombreArea", nullable = false, length = 100)
  private String nombreArea;

  @Column(name = "idUsuarioCreador", nullable = false)
  private Integer idUsuarioCreador;

  @Column(name = "idUsuarioModificador", nullable = true)
  private Integer idUsuarioModificador;

  @Column(name = "estadoRegistro", nullable = false, length = 4)
  private String estadoRegistro;

  @Column(name = "fechaCreacion", nullable = false)
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @Column(name = "fechaModificacion", nullable = true)
  private LocalDateTime fechaModificacion;
}
