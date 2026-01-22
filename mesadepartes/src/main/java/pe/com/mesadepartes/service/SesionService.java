package pe.com.mesadepartes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pe.com.mesadepartes.dtos.expediente.EditArchivoSesion;
import pe.com.mesadepartes.dtos.sesion.SesionListItem;
import pe.com.mesadepartes.entity.ExpedienteSesion;

@Service
@Transactional
public class SesionService {

  public static final String ESTADO_ACTIVO = "ACT";
  public static final String ESTADO_INACTIVO = "INAC";

  @PersistenceContext
  private EntityManager em;

  public Page<SesionListItem> listarSesiones(Pageable pageable, Integer idExpediente) {
    String jpql = """
          SELECT new pe.com.mesadepartes.dtos.sesion.SesionListItem(
            s.idExpedienteSesion,
            s.estadoSesion,
            s.fechaSesion,
            s.resolucionSesion,
            s.detallesSesion,
            s.secuencia
          )
          FROM ExpedienteSesion s
          WHERE s.expediente.idExpediente = :idExpediente
          ORDER BY s.idExpedienteSesion DESC
        """;

    List<SesionListItem> rows = em.createQuery(jpql, SesionListItem.class)
        .setParameter("idExpediente", idExpediente)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();

    String jpqlCount = """
          SELECT COUNT(s)
          FROM ExpedienteSesion s
        """;

    long total = em.createQuery(jpqlCount, Long.class)
        .getSingleResult();

    return new PageImpl<>(rows, pageable, total);
  }

  public List<SesionListItem> findSesionesByExpediente(Integer idExpediente) {
    String jpql = """
        SELECT new pe.com.mesadepartes.dtos.sesion.SesionListItem(
          s.idExpedienteSesion,
          s.estadoSesion,
          s.fechaSesion,
          s.resolucionSesion,
          s.detallesSesion,
          s.secuencia
        )
        FROM ExpedienteSesion s
        WHERE s.expediente.idExpediente = :idExpediente
        ORDER BY s.idExpedienteSesion DESC
      """;
    return em.createQuery(jpql, SesionListItem.class)
        .setParameter("idExpediente", idExpediente)
        .getResultList();
  }

  public Integer findMaxSecuenciaByExpediente(Integer idExpediente) {
    String jpql = "SELECT MAX(s.secuencia) FROM ExpedienteSesion s WHERE s.expediente.idExpediente = :idExpediente";
    Integer maxSecuencia = em.createQuery(jpql, Integer.class)
        .setParameter("idExpediente", idExpediente)
        .getSingleResult();
    return maxSecuencia == null ? 0 : maxSecuencia;
  }

  public ExpedienteSesion crearSesion(ExpedienteSesion sesion) {
    em.persist(sesion);
    em.flush();
    return this.buscarPorId(sesion.getIdExpedienteSesion());
  }

  public ExpedienteSesion buscarPorId(Integer id) {
    return em.find(ExpedienteSesion.class, id);
  }

  public ExpedienteSesion actualizarSesion(EditArchivoSesion form) {
    ExpedienteSesion sesion = em.find(ExpedienteSesion.class, form.getIdExpedienteSesion());
    if (sesion != null) {
      sesion.setEstadoSesion(form.getEstadoSesion());
      sesion.setDetallesSesion(form.getDetallesSesion());
      sesion.setFechaSesion(form.getFechaSesion());
      sesion.setResolucionSesion(form.getResolucionSesion());
      sesion.setFechaModificacion(LocalDateTime.now());
      sesion.setIdUsuarioModificador(1);
      em.merge(sesion);
      em.flush();
    }
    return sesion;
  }

  public void deleteById(Integer id) {
    ExpedienteSesion sesion = em.find(ExpedienteSesion.class, id);
    if (sesion != null) {
      em.remove(sesion);
    }
  }
}
