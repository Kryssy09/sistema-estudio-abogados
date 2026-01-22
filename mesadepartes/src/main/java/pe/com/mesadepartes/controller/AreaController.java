package pe.com.mesadepartes.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import pe.com.mesadepartes.dtos.area.AreaCreateForm;
import pe.com.mesadepartes.dtos.area.AreaEditForm;
import pe.com.mesadepartes.dtos.area.AreaListItem;
import pe.com.mesadepartes.entity.Area;
import pe.com.mesadepartes.service.AreaService;

@Controller
@RequestMapping("/areas")
public class AreaController {

  private final AreaService areaService;

  public AreaController(AreaService areaService) {
    this.areaService = areaService;
  }

  private int normPage(int p) {
    return Math.max(p, 0);
  }

  private int normSize(int s) {
    return Math.max(s, 1);
  }

  private String renderTabla(int page, int size, String q, Model model) {
    page = normPage(page);
    size = normSize(size);

    Pageable pageable = PageRequest.of(page, size);
    Page<AreaListItem> areasPage = areaService.buscarAreas(q, pageable);

    if (page > 0 && areasPage.getContent().isEmpty() && areasPage.getTotalElements() > 0) {
      page = page - 1;
      pageable = PageRequest.of(page, size);
      areasPage = areaService.buscarAreas(q, pageable);
    }

    model.addAttribute("areasPage", areasPage);
    model.addAttribute("q", q);
    model.addAttribute("size", size);
    model.addAttribute("activeSection", "areas");
    return "areas/_tabla :: tabla";
  }

  @GetMapping
  public String listarInicial(Model model) {
    int page = 0;
    int size = 5;
    String q = null;

    Pageable pageable = PageRequest.of(page, size);
    Page<AreaListItem> areasPage = areaService.buscarAreas(q, pageable);

    model.addAttribute("areasPage", areasPage);
    model.addAttribute("q", q);
    model.addAttribute("size", size);
    model.addAttribute("activeSection", "areas");
    return "areas/lista";
  }

  @PostMapping
  public String listarParcial(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String q,
      Model model) {
    return renderTabla(page, size, q, model);
  }

  @PostMapping("/{id}/activar")
  public String toggleActivo(
      @PathVariable Integer id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String q,
      Model model) {
    areaService.toggleActivo(id);
    return renderTabla(page, size, q, model);
  }

  @DeleteMapping("/{id}")
  public String eliminar(
      @PathVariable Integer id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String q,
      Model model,
      HttpServletResponse resp) {
    try {
      areaService.eliminar(id);
      model.addAttribute("toastSuccess", "Área eliminada correctamente.");
    } catch (Exception e) {
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      model.addAttribute("toastError", "Error al eliminar el área: " + e.getMessage());
    }
    return renderTabla(page, size, q, model);
  }

  // CREAR: cargar formulario de creacion
  @GetMapping("/nuevo")
  public String nuevo(Model model) {
    if (!model.containsAttribute("form")) {
      model.addAttribute("form", new AreaCreateForm());
    }
    model.addAttribute("mode", "create");
    return "areas/_form :: form";
  }

  // EDITAR: cargar formulario de edicion
  @GetMapping("/{id}/editar")
  public String formEditar(@PathVariable Integer id, Model model) {
    Area area = areaService.cargarArea(id);
    if (area == null) {
      model.addAttribute("errorMessage", "Área no encontrada");
      model.addAttribute("form", new AreaCreateForm());
      model.addAttribute("mode", "create");
      return "areas/_form :: form";
    }
    AreaEditForm form = new AreaEditForm();
    form.setIdArea(area.getIdArea());
    form.setNombreArea(area.getNombreArea());
    model.addAttribute("form", form);
    model.addAttribute("mode", "edit");
    return "areas/_form :: form";
  }

  // CREAR (desde modal)
  @PostMapping("/nuevo")
  public String crearArea(
      @Valid @ModelAttribute("form") AreaCreateForm data,
      BindingResult br,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String q,
      Model model,
      @RequestHeader(value = "HX-Request", required = false) String hx,
      HttpServletResponse resp) {

    if (br.hasErrors()) {
      model.addAttribute("mode", "create");
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      return "areas/_form :: form";
    }

    try {
      areaService.crear(data);
      // Actualizamos tabla
      String tabla = renderTabla(page, size, q, model);
      // Dispara evento para cerrar modal + toast
      resp.setHeader("HX-Trigger", "{\"areaSaved\":{\"message\":\"Área creada correctamente.\"}}");
      return tabla;
    } catch (Exception e) {
      model.addAttribute("mode", "create");
      model.addAttribute("errorMessage", "Error al crear el área: " + e.getMessage());
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      resp.setHeader("HX-Trigger", "{\"areaError\":{\"message\":\"No se pudo crear el área.\"}}");
      return "areas/_form :: form";
    }
  }

  // EDITAR (desde modal)
  @PostMapping("/{id}")
  public String editarAreaDesdeModal(
      @PathVariable Integer id,
      @Valid @ModelAttribute("form") AreaEditForm data,
      BindingResult br,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String q,
      Model model,
      HttpServletResponse resp) {

    if (!id.equals(data.getIdArea())) {
      br.reject("idMismatch", "El ID de la URL no coincide con el del formulario.");
    }
    if (br.hasErrors()) {
      model.addAttribute("mode", "edit");
      resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
      return "areas/_form :: form";
    }

    try {
      areaService.actualizarArea(data);
      String tabla = renderTabla(page, size, q, model);
      resp.setHeader("HX-Trigger", "{\"areaSaved\":{\"message\":\"Área actualizada correctamente.\"}}");
      return tabla;
    } catch (Exception e) {
      model.addAttribute("mode", "edit");
      model.addAttribute("errorMessage", "Error al actualizar el área: " + e.getMessage());
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      resp.setHeader("HX-Trigger", "{\"areaError\":{\"message\":\"No se pudo actualizar el área.\"}}");
      return "areas/_form :: form";
    }
  }
}
