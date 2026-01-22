package pe.com.mesadepartes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRolId implements Serializable {
  @Column(name = "id_usuario", nullable = false)
  private Integer idUsuario;
  @Column(name = "id_rol", nullable = false)
  private Integer idRol;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof UsuarioRolId that))
      return false;
    return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(idRol, that.idRol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idUsuario, idRol);
  }

}
