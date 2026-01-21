package pe.com.mesadepartes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.entity.Solicitante;

@Repository
public interface ExpedienteRepository extends JpaRepository<Expediente, Integer> {

  @Query("SELECT e FROM Expediente e WHERE e.estadoRegistro = 'ACT' and ( " +
      "LOWER(e.codigoSeguimiento) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
      "LOWER(e.tipoExpediente) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
      "LOWER(e.estadoExpediente) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
      "LOWER(e.reseniaSolicitud) LIKE LOWER(CONCAT('%', :busqueda, '%')) )")
  Page<Expediente> buscarExpedientes(@Param("busqueda") String busqueda, Pageable pageable);

  // === Datos para el gráfico (MySQL/MariaDB) ===
  // Cuenta expedientes ACT del año actual agrupados por tipo
  @Query(value = """
      SELECT
        COALESCE(e.tipoExpediente, 'SIN_TIPO') AS label,
        COUNT(*) AS value
      FROM expediente e
      WHERE e.estadoRegistro = 'ACT'
        AND YEAR(e.fechaCreacion) = YEAR(CURDATE())
      GROUP BY COALESCE(e.tipoExpediente, 'SIN_TIPO')
      ORDER BY value DESC
      """, nativeQuery = true)
  List<Object[]> contarPorTipoAnioActual();

  @Query(value = """
      SELECT
        COALESCE(e.tipoExpediente, 'SIN_TIPO') AS label,
        COUNT(*) AS value
      FROM expediente e
      GROUP BY COALESCE(e.tipoExpediente, 'SIN_TIPO')
      ORDER BY value DESC
      """, nativeQuery = true)
  List<Object[]> contarPorTipoSinFiltros();

  // Método para encontrar el último expediente de un solicitante
  Optional<Expediente> findTopByPersonaSolicitanteOrderByIdExpedienteDesc(Solicitante solicitante);

  // Método para contar expedientes por solicitante
  @Query("SELECT COUNT(e) FROM Expediente e WHERE e.personaSolicitante.idSolicitante = :idSolicitante AND e.estadoRegistro = 'ACT'")
  Long countByIdSolicitante(@Param("idSolicitante") Integer idSolicitante);

  // Método para contar expedientes por estado
  long countByEstadoExpediente(String estadoExpediente);


}
