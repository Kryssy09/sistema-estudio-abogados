package pe.com.mesadepartes.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Solicitante implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idSolicitante")
  private Integer idSolicitante;

  // @ManyToOne(fetch = FetchType.EAGER)
  @ManyToOne(optional = false)
  // @JoinColumn(name = "idPersona", nullable = false)
  @JoinColumn(name = "idPersona")
  @JsonManagedReference
  private Persona persona;

  // @Column(name = "idUsuarioCreador", nullable = false)
  @Column(name = "idUsuarioCreador")
  private Integer idUsuarioCreador;

  @Column(name = "fechaCreacion", nullable = false)
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  // @Column(name = "estadoRegistro", nullable = false, length = 4)
  @Column(name = "estadoRegistro", nullable = false, length = 20)
  private String estadoRegistro;

  @Column(name = "idUsuarioModificador", nullable = true)
  private Integer idUsuarioModificador;

  @Column(name = "fechaModificacion", nullable = true)
  private LocalDateTime fechaModificacion;
}
