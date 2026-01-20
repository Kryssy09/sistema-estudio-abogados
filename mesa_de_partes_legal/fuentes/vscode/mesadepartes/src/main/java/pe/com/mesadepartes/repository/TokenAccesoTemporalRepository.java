package pe.com.mesadepartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.com.mesadepartes.entity.TokenAccesoTemporal;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenAccesoTemporalRepository extends JpaRepository<TokenAccesoTemporal, Long> {

    @Query("SELECT t FROM TokenAccesoTemporal t WHERE t.token = :token AND t.estado = 'ACTIVO' AND t.fechaVencimiento > :fechaActual")
    Optional<TokenAccesoTemporal> findTokenValido(@Param("token") String token, @Param("fechaActual") LocalDateTime fechaActual);

    @Query("SELECT t FROM TokenAccesoTemporal t WHERE t.idSolicitante = :idSolicitante AND t.idExpediente = :idExpediente AND t.estado = 'ACTIVO' AND t.fechaVencimiento > :fechaActual")
    Optional<TokenAccesoTemporal> findAccesoVigentePorSolicitanteYExpediente(
            @Param("idSolicitante") Integer idSolicitante,
            @Param("idExpediente") Integer idExpediente,
            @Param("fechaActual") LocalDateTime fechaActual
    );

    Optional<TokenAccesoTemporal> findByToken(String token);
}