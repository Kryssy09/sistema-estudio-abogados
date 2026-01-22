package pe.com.mesadepartes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pe.com.mesadepartes.dtos.area.AreaCreateForm;
import pe.com.mesadepartes.dtos.area.AreaEditForm;
import pe.com.mesadepartes.dtos.area.AreaListItem;
import pe.com.mesadepartes.entity.Area;

@Service
@Transactional
public class AreaService {
  public static final String ESTADO_ACTIVO = "ACT";
  public static final String ESTADO_INACTIVO = "INAC";

  @PersistenceContext
  private EntityManager em;

  public Integer crear(AreaCreateForm data) {
    final int USER_CREATOR = 1;

    Area area = new Area();
    area.setNombreArea(data.getNombreArea().trim());
    area.setEstadoRegistro(ESTADO_ACTIVO);
    area.setIdUsuarioCreador(USER_CREATOR);
    area.setIdUsuarioModificador(null);
    area.setFechaCreacion(LocalDateTime.now());
    area.setFechaModificacion(null);

    em.persist(area);
    em.flush();
    return area.getIdArea();
  }

  public Optional<Area> buscarPorId(Integer id) {
    return Optional.ofNullable(em.find(Area.class, id));
  }

  public Page<AreaListItem> buscarAreas(String q, Pageable pageable) {
    final String filtro = (q == null || q.isBlank()) ? null : q.trim().toLowerCase();

    String jpql = """
          SELECT new pe.com.mesadepartes.dtos.area.AreaListItem(
            a.idArea,
            a.nombreArea,
            CASE WHEN a.estadoRegistro = :ACT THEN true ELSE false END
          )
          FROM Area a
          WHERE (:filtro IS NULL OR
                 LOWER(CONCAT(COALESCE(a.nombreArea,'')))
                 LIKE CONCAT('%', :filtro, '%'))
          ORDER BY a.idArea DESC
        """;

    List<AreaListItem> rows = em.createQuery(jpql, AreaListItem.class)
        .setParameter("filtro", filtro)
        .setParameter("ACT", ESTADO_ACTIVO)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();

    // COUNT
    String jpqlCount = """
          SELECT COUNT(a)
          FROM Area a
          WHERE (:filtro IS NULL OR
                 LOWER(CONCAT(COALESCE(a.nombreArea,'')))
                 LIKE CONCAT('%', :filtro, '%'))
        """;

    long total = em.createQuery(jpqlCount, Long.class)
        .setParameter("filtro", filtro)
        .getSingleResult();

    return new PageImpl<>(rows, pageable, total);
  }

  public Area cargarArea(Integer id) {
    return em.find(Area.class, id);
  }

  public List<Area> listarAreas() {
    String jpql = """
          SELECT a
          FROM Area a
          WHERE a.estadoRegistro = :est
          ORDER BY a.nombreArea ASC
        """;
    return em.createQuery(jpql, Area.class)
        .setParameter("est", ESTADO_ACTIVO)
        .getResultList();
  }

  public void eliminar(Integer id) {
    Area area = em.find(Area.class, id);
    if (area != null) {
      em.remove(area);
    }
  }

  public void toggleActivo(Integer id) {
    Area u = em.find(Area.class, id);
    if (u == null)
      return;
    if (ESTADO_ACTIVO.equalsIgnoreCase(u.getEstadoRegistro())) {
      u.setEstadoRegistro(ESTADO_INACTIVO);
    } else {
      u.setEstadoRegistro(ESTADO_ACTIVO);
    }
    u.setFechaModificacion(LocalDateTime.now());
    em.merge(u);
  }

  public void actualizarArea(AreaEditForm data) {
    Area area = em.find(Area.class, data.getIdArea());
    area.setNombreArea(data.getNombreArea().trim());
    area.setFechaModificacion(LocalDateTime.now());
    em.merge(area);
  }
}
