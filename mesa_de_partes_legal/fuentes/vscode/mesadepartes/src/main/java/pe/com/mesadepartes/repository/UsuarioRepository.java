package pe.com.mesadepartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.com.mesadepartes.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Boolean existsByNombreUsuario(String nombreUsuario);

    // Método para contar usuarios por estado de registro
    long countByEstadoRegistro(String estadoRegistro);

}