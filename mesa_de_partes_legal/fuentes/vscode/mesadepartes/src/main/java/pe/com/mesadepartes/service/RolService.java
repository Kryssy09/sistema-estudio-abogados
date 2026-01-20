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
import pe.com.mesadepartes.dtos.rol.RolCreateForm;
import pe.com.mesadepartes.dtos.rol.RolEditForm;
import pe.com.mesadepartes.dtos.rol.RolListItem;
import pe.com.mesadepartes.entity.Rol;

@Service
@Transactional
public class RolService {
  public static final String ESTADO_ACTIVO = "ACT";
  public static final String ESTADO_INACTIVO = "INAC";

  @PersistenceContext
  private EntityManager em;

  public Integer crear(RolCreateForm data) {
    final int USER_CREATOR = 1;

    Rol rol = new Rol();
    rol.setNombreRol(data.getNombreRol().trim());
    rol.setEstadoRegistro(ESTADO_ACTIVO);
    rol.setIdUsuarioCreador(USER_CREATOR);
    rol.setIdUsuarioModificador(null);
    rol.setFechaCreacion(LocalDateTime.now());
    rol.setFechaModificacion(null);

    em.persist(rol);
    em.flush();
    return rol.getId();
  }

  public Optional<Rol> buscarPorId(Integer id) {
    return Optional.ofNullable(em.find(Rol.class, id));
  }

  public Page<RolListItem> buscarRoles(String q, Pageable pageable) {
    final String filtro = (q == null || q.isBlank()) ? null : q.trim().toLowerCase();

    String jpql = """
          SELECT new pe.com.mesadepartes.dtos.rol.RolListItem(
            r.idRol,
            r.nombreRol,
            CASE WHEN r.estadoRegistro = :ACT THEN true ELSE false END
          )
          FROM Rol r
          WHERE (:filtro IS NULL OR
                 LOWER(CONCAT(COALESCE(r.nombreRol,'')))
                 LIKE CONCAT('%', :filtro, '%'))
          ORDER BY r.idRol DESC
        """;

    List<RolListItem> rows = em.createQuery(jpql, RolListItem.class)
        .setParameter("filtro", filtro)
        .setParameter("ACT", ESTADO_ACTIVO)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();

    // COUNT
    String jpqlCount = """
          SELECT COUNT(r)
          FROM Rol r
          WHERE (:filtro IS NULL OR
                 LOWER(CONCAT(COALESCE(r.nombreRol,'')))
                 LIKE CONCAT('%', :filtro, '%'))
        """;

    long total = em.createQuery(jpqlCount, Long.class)
        .setParameter("filtro", filtro)
        .getSingleResult();

    return new PageImpl<>(rows, pageable, total);
  }

  public Rol cargarRol(Integer id) {
    return em.find(Rol.class, id);
  }

  public List<Rol> listarActivos() {
    String jpql = """
          SELECT r
          FROM Rol r
          WHERE r.estadoRegistro = :est
          ORDER BY r.nombreRol ASC
        """;
    return em.createQuery(jpql, Rol.class)
        .setParameter("est", "ACT")
        .getResultList();
  }

  public void eliminar(Integer id) {
    Rol rol = em.find(Rol.class, id);
    if (rol != null) {
      em.remove(rol);
    }
  }

  public void toggleActivo(Integer id) {
    Rol rol = em.find(Rol.class, id);
    if (rol == null)
      return;
    if (ESTADO_ACTIVO.equalsIgnoreCase(rol.getEstadoRegistro())) {
      rol.setEstadoRegistro(ESTADO_INACTIVO);
    } else {
      rol.setEstadoRegistro(ESTADO_ACTIVO);
    }
    rol.setFechaModificacion(LocalDateTime.now());
    em.merge(rol);
  }

  public void actualizarRol(RolEditForm data) {
    Rol rol = em.find(Rol.class, data.getIdRol());
    rol.setNombreRol(data.getNombreRol().trim());
    rol.setFechaModificacion(LocalDateTime.now());
    em.merge(rol);
  }
}
