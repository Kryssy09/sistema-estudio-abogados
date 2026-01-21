package pe.com.mesadepartes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.mesadepartes.dto.SolicitanteRowDto;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.entity.Persona;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitanteRepository extends JpaRepository<Solicitante, Integer> {

    // -------- EXISTENTES (OK) --------
    @Query("""
           select s
           from Solicitante s
           join fetch s.persona p
           where s.idSolicitante = :id
           """)
    Optional<Solicitante> findByIdFetchPersona(@Param("id") Integer id);

    @Query(value = """
           select s
           from Solicitante s
           join s.persona p
           where
                lower(p.numeroDocumento) like lower(concat('%', :q, '%'))
             or lower(p.nombres)          like lower(concat('%', :q, '%'))
             or lower(p.apellidoPaterno)  like lower(concat('%', :q, '%'))
             or lower(p.apellidoMaterno)  like lower(concat('%', :q, '%'))
           """,
            countQuery = """
           select count(s)
           from Solicitante s
           join s.persona p
           where
                lower(p.numeroDocumento) like lower(concat('%', :q, '%'))
             or lower(p.nombres)          like lower(concat('%', :q, '%'))
             or lower(p.apellidoPaterno)  like lower(concat('%', :q, '%'))
             or lower(p.apellidoMaterno)  like lower(concat('%', :q, '%'))
           """)
    Page<Solicitante> buscarSolicitantes(@Param("q") String q, Pageable pageable);

    // ====== ÚNICA versión para el dashboard (NATIVA y robusta) ======
    // IMPORTANTE: Asegúrate que estos nombres son los de **tus tablas reales**:
    //   - solicitante(idSolicitante, idPersona, fechaCreacion)
    //   - persona(idPersona, apellidoPaterno, apellidoMaterno, nombres, numeroDocumento, fechaCreacion)
    //   - expediente(idExpediente, idSolicitante)
    @Query(value = """
        SELECT
          s.idSolicitante                                                             AS id,
          CONCAT(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres)           AS nombreCompleto,
          p.numeroDocumento                                                           AS dni,
          p.correoElectronico                                                         AS correoElectronico,
          p.telefonoPersonal                                                          AS telefonoPersonal,
          DATE_FORMAT(COALESCE(s.fechaCreacion, p.fechaCreacion), '%Y-%m-%d')         AS fechaRegistro,
          (SELECT COUNT(e.idExpediente) FROM expediente e WHERE e.idSolicitante = s.idSolicitante) AS expedientes
        FROM solicitante s
        JOIN persona p ON p.idPersona = s.idPersona
        WHERE s.estadoRegistro = 'ACT'
        ORDER BY s.idSolicitante ASC
        """,
            nativeQuery = true)
    List<Object[]> listarFilasRaw();    // <--- ¡OJO! Object[] aquí, no Object

    // Adaptador: convierte la fila cruda a nuestro DTO plano
    default List<SolicitanteRowDto> listarFilas() {
        return listarFilasRaw().stream()
                .map(r -> new SolicitanteRowDto(
                        ((Number) r[0]).intValue(),           // id
                        (String) r[1],                        // nombreCompleto
                        (String) r[2],                        // dni
                        (String) r[3],                        // correoElectronico
                        (String) r[4],                        // telefonoPersonal
                        (String) r[5],                        // fechaRegistro
                        r[6] == null ? 0L : ((Number) r[6]).longValue() // expedientes
                ))
                .toList();
    }

    // Método para encontrar solicitante por persona
    Optional<Solicitante> findByPersona(Persona persona);
}