package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Invitado implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idInvitado")
  private Integer idInvitado;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "idPersona", nullable = false)
  private Persona persona;

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
