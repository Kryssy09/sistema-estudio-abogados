package pe.com.mesadepartes.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.mesadepartes.dtos.expediente.DocItem;
import pe.com.mesadepartes.dtos.expediente.EditArchivoSesion;
import pe.com.mesadepartes.dtos.expediente.SubirArchivos;
import pe.com.mesadepartes.entity.ExpedienteSesion;
import pe.com.mesadepartes.entity.ExpedienteSesionArchivo;
import pe.com.mesadepartes.service.ExpedienteSesionArchivoService;
import pe.com.mesadepartes.service.FileStorageService;
import pe.com.mesadepartes.service.SesionService;

@RestController
public class SesionesController {

  private final SesionService sesionService;
  private final ExpedienteSesionArchivoService expedienteSesionArchivoService;
  private final FileStorageService fileStorageService;

  private static final Map<String, String> ESTADO_LABELS = Map.of(
      "PROG", "Programada / Agendada",
      "EN_CUR", "En Curso",
      "CONCLU", "Concluida",
      "ANUL", "Anulada");

  public SesionesController(SesionService sesionService,
      ExpedienteSesionArchivoService expedienteSesionArchivoService,
      FileStorageService fileStorageService) {
    this.sesionService = sesionService;
    this.expedienteSesionArchivoService = expedienteSesionArchivoService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping("/sesiones/{id}/detalle")
  public Map<String, Object> detalle(@PathVariable Integer id) {

    Map<Integer, String> RESOLUCION_LABELS = Map.of(
        1, "ACUERDO TOTAL",
        2, "ACUERDO PARCIAL",
        3, "SIN ACUERDO");
    ExpedienteSesion sesion = sesionService.buscarPorId(id);
    if (sesion == null)
      throw new IllegalArgumentException("No existe sesión " + id);
    List<ExpedienteSesionArchivo> archivos = expedienteSesionArchivoService.listarArchivosPorSesion(id);

    return Map.of(
        "id", sesion.getIdExpedienteSesion(),
        "secuencia", sesion.getSecuencia(),
        "fechaSesion", sesion.getFechaSesion(),
        "estadoSesion", ESTADO_LABELS.get(sesion.getEstadoSesion()),
        "detallesSesion", sesion.getDetallesSesion(),
        "resolucionSesion", RESOLUCION_LABELS.get(sesion.getResolucionSesion()),
        "archivos", archivos.stream().map(a -> Map.of(
            "id", a.getIdExpedienteSesionArchivo(),
            "nombreDocumento", a.getNombreDocumento(),
            "rutaArchivo", a.getRutaArchivo(),
            "fechaCreacion", a.getFechaCreacion())).toList());
  }

  @PostMapping("/sesiones/editar")
  public Map<String, Object> editar(
      @ModelAttribute EditArchivoSesion form) {

    ExpedienteSesion sesion = sesionService.actualizarSesion(form);

    // Lógica para editar la sesión
    return Map.of("ok", true,
        "sesion", Map.of(
            "id", sesion.getIdExpedienteSesion(),
            "fechaSesion", sesion.getFechaSesion().toString(),
            "estadoSesion", sesion.getEstadoSesion(),
            "resolucionSesion", sesion.getResolucionSesion(),
            "detallesSesion", sesion.getDetallesSesion()));
  }

  @PostMapping("/sesiones/{id}/archivos")
  public Map<String, Object> subirArchivos(@PathVariable Integer id,
      @ModelAttribute SubirArchivos form) {
    ExpedienteSesion sesion = sesionService.buscarPorId(id);
    if (sesion == null)
      throw new IllegalArgumentException("No existe sesión " + id);

    List<Map<String, Object>> creados = new ArrayList<>();
    if (form.getDocs() != null) {
      for (DocItem item : form.getDocs()) {
        if (item == null || item.getFile() == null || item.getFile().isEmpty())
          continue;

        try {
          String ruta = fileStorageService.guardarArchivoSesion(id, item.getFile());

          ExpedienteSesionArchivo expedienteSesionArchivo = new ExpedienteSesionArchivo();
          expedienteSesionArchivo.setSesion(sesion);
          expedienteSesionArchivo.setRutaArchivo(ruta);
          expedienteSesionArchivo.setNombreDocumento(item.getTitulo());
          expedienteSesionArchivo.setFechaCreacion(LocalDateTime.now());
          expedienteSesionArchivo.setIdUsuarioCreador(1);
          expedienteSesionArchivo.setEstadoRegistro("ACT");
          expedienteSesionArchivoService.crearArchivo(expedienteSesionArchivo);

          creados.add(Map.of(
              "id", expedienteSesionArchivo.getIdExpedienteSesionArchivo(),
              "nombreDocumento", expedienteSesionArchivo.getNombreDocumento(),
              "rutaArchivo", expedienteSesionArchivo.getRutaArchivo()));
        } catch (Exception e) {
          System.err.println("Error al guardar archivo: " + e.getMessage());
        }
      }
    }
    return Map.of("ok", true, "archivos", creados);
  }

  @DeleteMapping("/sesiones/archivos/{idArchivo}")
  public Map<String, Object> eliminarArchivo(@PathVariable Integer idArchivo) {
    expedienteSesionArchivoService.eliminarArchivo(idArchivo);
    return Map.of("ok", true, "idEliminado", idArchivo);
  }

}
