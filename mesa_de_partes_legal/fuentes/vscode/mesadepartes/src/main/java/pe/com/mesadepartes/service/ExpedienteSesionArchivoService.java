package pe.com.mesadepartes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import pe.com.mesadepartes.entity.ExpedienteSesionArchivo;

@Service
@Transactional
@AllArgsConstructor
public class ExpedienteSesionArchivoService {

  private final FileStorageService fileStorageService;

  public static final String ESTADO_ACTIVO = "ACT";
  public static final String ESTADO_INACTIVO = "INAC";

  @PersistenceContext
  private EntityManager em;

  public void crearArchivo(ExpedienteSesionArchivo sesion) {
    em.persist(sesion);
    em.flush();
  }

  public List<ExpedienteSesionArchivo> listarArchivosPorSesion(Integer idExpedienteSesion) {
    String jpql = "SELECT esa FROM ExpedienteSesionArchivo esa " +
        "WHERE esa.sesion.idExpedienteSesion = :idExpedienteSesion " +
        "AND esa.estadoRegistro = :estadoRegistro";

    return em.createQuery(jpql, ExpedienteSesionArchivo.class)
        .setParameter("idExpedienteSesion", idExpedienteSesion)
        .setParameter("estadoRegistro", ESTADO_ACTIVO)
        .getResultList();
  }

  public void eliminarArchivo(Integer idArchivo) {
    ExpedienteSesionArchivo archivo = em.find(ExpedienteSesionArchivo.class, idArchivo);

    fileStorageService.eliminarArchivo(archivo.getRutaArchivo());
    em.remove(archivo);
  }
}
