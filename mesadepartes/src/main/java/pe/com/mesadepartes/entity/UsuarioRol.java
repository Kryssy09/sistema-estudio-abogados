package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioRol implements Serializable {

  @EmbeddedId
  private UsuarioRolId id;

  @MapsId("idUsuario")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_usuario", nullable = false)
  private Usuario usuario;

  @MapsId("idRol")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_rol", nullable = false)
  private Rol rol;

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
