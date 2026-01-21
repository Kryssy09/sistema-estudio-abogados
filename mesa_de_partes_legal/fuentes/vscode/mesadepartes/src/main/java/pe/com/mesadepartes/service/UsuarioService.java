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
import pe.com.mesadepartes.dtos.usuarios.UsuarioCreateForm;
import pe.com.mesadepartes.dtos.usuarios.UsuarioEditForm;
import pe.com.mesadepartes.dtos.usuarios.UsuarioListItem;
import pe.com.mesadepartes.entity.Persona;
import pe.com.mesadepartes.entity.Rol;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.entity.UsuarioRol;
import pe.com.mesadepartes.entity.UsuarioRolId;

@Service
@Transactional
public class UsuarioService {

  public static final String ESTADO_ACTIVO = "ACT";
  public static final String ESTADO_INACTIVO = "INAC";

  @PersistenceContext
  private EntityManager em;

  public Integer crearUsuario(UsuarioCreateForm data, String rutaFoto) {
    final int USER_CREATOR = 1;

    Persona nuevaPersona = new Persona();

    nuevaPersona.setApellidoPaterno(data.getApellidoPaterno().trim());
    nuevaPersona.setApellidoMaterno(data.getApellidoMaterno().trim());
    nuevaPersona.setNombres(data.getNombres().trim());
    nuevaPersona.setTipoDocumentoIdentidad(data.getTipoDocumentoIdentidad());
    nuevaPersona.setNumeroDocumento(data.getNumeroDocumento().trim());
    nuevaPersona.setCorreoElectronico(data.getCorreoElectronico());
    nuevaPersona.setTelefonoPersonal(data.getTelefonoPersonal());
    nuevaPersona.setSexo(data.getSexo());

    nuevaPersona.setRutaFoto(null);
    nuevaPersona.setEstadoRegistro(ESTADO_ACTIVO);
    nuevaPersona.setIdUsuarioCreador(USER_CREATOR);
    nuevaPersona.setIdUsuarioModificador(null);

    if (rutaFoto != null && !rutaFoto.isBlank()) {
      nuevaPersona.setRutaFoto(rutaFoto);
    }

    em.persist(nuevaPersona);

    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setPersona(nuevaPersona);
    nuevoUsuario.setNombreUsuario(data.getNombreUsuario().trim());
    nuevoUsuario.setClave(data.getClave().trim());
    nuevoUsuario.setEstadoRegistro(ESTADO_ACTIVO);

    em.persist(nuevoUsuario);
    em.flush();

    // Asignar roles
    if (data.getRolesIds() != null) {
      for (Integer rolId : data.getRolesIds()) {
        Rol rol = em.getReference(Rol.class, rolId);

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(new UsuarioRolId(nuevoUsuario.getIdUsuario(), rolId));
        usuarioRol.setUsuario(nuevoUsuario);
        usuarioRol.setRol(rol);
        usuarioRol.setEstadoRegistro(ESTADO_ACTIVO);
        usuarioRol.setIdUsuarioCreador(USER_CREATOR);
        usuarioRol.setIdUsuarioModificador(null);

        em.persist(usuarioRol);
      }

    }
    return nuevoUsuario.getIdUsuario();
  }

  public Optional<Usuario> buscarPorId(Integer id) {
    return Optional.ofNullable(em.find(Usuario.class, id));
  }

  public Page<UsuarioListItem> buscarUsuarios(String q, Pageable pageable) {
    final String filtro = (q == null || q.isBlank()) ? null : q.trim().toLowerCase();

    // 1. Contar total
    String jpqlCount = """
          SELECT COUNT(u)
          FROM Usuario u
          JOIN u.persona p
          WHERE (:filtro IS NULL OR
                 LOWER(CONCAT(COALESCE(p.apellidoPaterno,''),' ',COALESCE(p.apellidoMaterno,''),' ',COALESCE(p.nombres,''),' ',COALESCE(u.nombreUsuario,'')))
                 LIKE CONCAT('%', :filtro, '%'))
        """;

    long total = em.createQuery(jpqlCount, Long.class)
        .setParameter("filtro", filtro)
        .getSingleResult();

    // 2. Obtener entidades paginadas
    String jpql = """
          SELECT u
          FROM Usuario u
          JOIN FETCH u.persona p
          WHERE (:filtro IS NULL OR
                 LOWER(CONCAT(COALESCE(p.apellidoPaterno,''),' ',COALESCE(p.apellidoMaterno,''),' ',COALESCE(p.nombres,''),' ',COALESCE(u.nombreUsuario,'')))
                 LIKE CONCAT('%', :filtro, '%'))
          ORDER BY u.idUsuario DESC
        """;

    List<Usuario> usuarios = em.createQuery(jpql, Usuario.class)
        .setParameter("filtro", filtro)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();

    // 3. Mapear a DTO y cargar roles
    List<UsuarioListItem> dtos = usuarios.stream().map(u -> {
      // Cargar roles (lazy loading o consulta explícita)
      List<String> roles = em.createQuery(
          "SELECT r.nombreRol FROM UsuarioRol ur JOIN ur.rol r WHERE ur.usuario.idUsuario = :uid AND ur.estadoRegistro = :act",
          String.class)
          .setParameter("uid", u.getIdUsuario())
          .setParameter("act", ESTADO_ACTIVO)
          .getResultList();

      return new UsuarioListItem(
          u.getIdUsuario(),
          u.getPersona().getApellidoPaterno(),
          u.getPersona().getApellidoMaterno(),
          u.getPersona().getNombres(),
          u.getNombreUsuario(),
          ESTADO_ACTIVO.equals(u.getEstadoRegistro()),
          roles);
    }).toList();

    return new PageImpl<>(dtos, pageable, total);
  }

  public List<Usuario> listarTodos() {
    String jpql = "SELECT u FROM Usuario u WHERE u.estadoRegistro = :est ORDER BY u.persona.apellidoPaterno ASC";
    return em.createQuery(jpql, Usuario.class)
        .setParameter("est", ESTADO_ACTIVO)
        .getResultList();
  }

  public void eliminar(Integer id) {
    Usuario usuario = em.find(Usuario.class, id);
    if (usuario != null) {
      em.remove(usuario);
    }
  }

  public void toggleActivo(Integer id) {
    Usuario u = em.find(Usuario.class, id);
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

  public List<Integer> rolesIdsDeUsuario(Integer idUsuario) {
    return em.createQuery(
        "select ur.id.idRol from UsuarioRol ur where ur.id.idUsuario = :uid",
        Integer.class)
        .setParameter("uid", idUsuario)
        .getResultList();
  }

  public Usuario cargarUsuario(Integer id) {
    return em.find(Usuario.class, id);
  }

  public void actualizarUsuario(UsuarioEditForm data, String rutaFoto) {
    final int USER_MOD = 1;

    Usuario usuario = em.find(Usuario.class, data.getIdUsuario());

    if (usuario == null) {
      throw new IllegalArgumentException("No se encontró el usuario con ID " + data.getIdUsuario());
    }

    Persona persona = usuario.getPersona();

    if (persona == null) {
      throw new IllegalArgumentException("No se encontró la persona asociada al usuario con ID " + data.getIdUsuario());
    }

    persona.setApellidoPaterno(data.getApellidoPaterno().trim());
    persona.setApellidoMaterno(data.getApellidoMaterno().trim());
    persona.setNombres(data.getNombres().trim());
    persona.setTipoDocumentoIdentidad(data.getTipoDocumentoIdentidad());
    persona.setNumeroDocumento(data.getNumeroDocumento().trim());
    persona.setCorreoElectronico(data.getCorreoElectronico());
    persona.setTelefonoPersonal(data.getTelefonoPersonal());
    persona.setSexo(data.getSexo());
    if (rutaFoto != null && !rutaFoto.isBlank()) {
      persona.setRutaFoto(rutaFoto);
    }

    persona.setIdUsuarioModificador(USER_MOD);
    persona.setFechaModificacion(LocalDateTime.now());
    em.merge(persona);

    usuario.setNombreUsuario(data.getNombreUsuario().trim());
    if (data.getClave() != null && !data.getClave().isBlank()) {
      usuario.setClave(data.getClave());
    }

    usuario.setFechaModificacion(LocalDateTime.now());
    em.merge(usuario);

    em.createQuery("DELETE FROM UsuarioRol ur WHERE ur.id.idUsuario = :uid")
        .setParameter("uid", data.getIdUsuario())
        .executeUpdate();

    if (data.getRolesIds() != null) {
      for (Integer rolId : data.getRolesIds()) {
        Rol rol = em.getReference(Rol.class, rolId);

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(new UsuarioRolId(usuario.getIdUsuario(), rolId));
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);
        usuarioRol.setEstadoRegistro(ESTADO_ACTIVO);
        usuarioRol.setIdUsuarioCreador(USER_MOD);
        usuarioRol.setIdUsuarioModificador(null);

        em.persist(usuarioRol);
      }
    }
  }
}
