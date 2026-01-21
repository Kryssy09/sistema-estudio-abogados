package pe.com.mesadepartes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pe.com.mesadepartes.entity.Invitado;

@Service
@Transactional
public class InvitadoService {
  public static final String ESTADO_ACTIVO = "ACT";
  public static final String ESTADO_INACTIVO = "INAC";

  @PersistenceContext
  private EntityManager em;

  public List<Invitado> listarActivos() {
    String jpql = """
          SELECT i
          FROM Invitado i
          WHERE i.estadoRegistro = :est
          ORDER BY i.persona.apellidoPaterno ASC, i.persona.apellidoMaterno ASC, i.persona.nombres ASC
        """;
    return em.createQuery(jpql, Invitado.class)
        .setParameter("est", ESTADO_ACTIVO)
        .getResultList();
  }

  public Invitado buscarPorId(Integer idInvitado) {
    return em.find(Invitado.class, idInvitado);
  }
}
