package pe.com.mesadepartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.mesadepartes.entity.UsuarioRol;
import pe.com.mesadepartes.entity.UsuarioRolId;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    List<UsuarioRol> findByUsuarioIdUsuario(Integer idUsuario);

    List<UsuarioRol> findByRolId(Integer idRol);
}