package pe.com.mesadepartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.mesadepartes.entity.ExpedienteMovimiento;

import java.util.List;

@Repository
public interface ExpedienteMovimientoRepository extends JpaRepository<ExpedienteMovimiento, Integer> {
    List<ExpedienteMovimiento> findByExpedienteIdExpedienteOrderByFechaCreacionDesc(Integer idExpediente);
}
