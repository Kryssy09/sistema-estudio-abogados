package pe.com.mesadepartes.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import pe.com.mesadepartes.dtos.rol.RolCreateForm;
import pe.com.mesadepartes.dtos.rol.RolEditForm;
import pe.com.mesadepartes.dtos.rol.RolListItem;
import pe.com.mesadepartes.entity.Rol;
import pe.com.mesadepartes.service.RolService;

@Controller
@RequestMapping("/roles")
public class RolController {

  private final RolService rolService;

  public RolController(RolService rolService) {
    this.rolService = rolService;
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
    Page<RolListItem> rolesPage = rolService.buscarRoles(q, pageable);

    if (page > 0 && rolesPage.getContent().isEmpty() && rolesPage.getTotalElements() > 0) {
      page = page - 1;
      pageable = PageRequest.of(page, size);
      rolesPage = rolService.buscarRoles(q, pageable);
    }

    model.addAttribute("rolesPage", rolesPage);
    model.addAttribute("q", q);
    model.addAttribute("size", size);
    model.addAttribute("activeSection", "roles");
    return "roles/_tabla :: tabla";
  }

  @GetMapping
  public String listarInicial(Model model) {
    int page = 0;
    int size = 5;
    String q = null;

    Pageable pageable = PageRequest.of(page, size);
    Page<RolListItem> rolesPage = rolService.buscarRoles(q, pageable);

    model.addAttribute("rolesPage", rolesPage);
    model.addAttribute("q", q);
    model.addAttribute("size", size);
    model.addAttribute("activeSection", "roles");
    return "roles/lista";
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
    rolService.toggleActivo(id);
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
      rolService.eliminar(id);
      model.addAttribute("toastSuccess", "Rol eliminado correctamente.");
    } catch (Exception e) {
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      model.addAttribute("toastError", "Error al eliminar el rol: " + e.getMessage());
    }
    return renderTabla(page, size, q, model);
  }

  // CREAR: cargar formulario de creacion
  @GetMapping("/nuevo")
  public String nuevo(Model model) {
    if (!model.containsAttribute("form")) {
      model.addAttribute("form", new RolCreateForm());
    }
    model.addAttribute("mode", "create");
    return "roles/_form :: form";
  }

  // EDITAR: cargar formulario de edicion
  @GetMapping("/{id}/editar")
  public String formEditar(@PathVariable Integer id, Model model) {
    Rol rol = rolService.cargarRol(id);
    if (rol == null) {
      model.addAttribute("errorMessage", "Rol no encontrado");
      model.addAttribute("form", new RolCreateForm());
      model.addAttribute("mode", "create");
      return "roles/_form :: form";
    }
    RolEditForm form = new RolEditForm();
    form.setIdRol(rol.getId());
    form.setNombreRol(rol.getNombreRol());
    model.addAttribute("form", form);
    model.addAttribute("mode", "edit");
    return "roles/_form :: form";
  }

  // CREAR (desde modal)
  @PostMapping("/nuevo")
  public String crearRol(
      @Valid @ModelAttribute("form") RolCreateForm data,
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
      return "roles/_form :: form";
    }

    try {
      rolService.crear(data);
      // Actualizamos tabla
      String tabla = renderTabla(page, size, q, model);
      // Dispara evento para cerrar modal + toast
      resp.setHeader("HX-Trigger", "{\"rolSaved\":{\"message\":\"Rol creado correctamente.\"}}");
      return tabla;
    } catch (Exception e) {
      model.addAttribute("mode", "create");
      model.addAttribute("errorMessage", "Error al crear el rol: " + e.getMessage());
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      resp.setHeader("HX-Trigger", "{\"rolError\":{\"message\":\"No se pudo crear el rol.\"}}");
      return "roles/_form :: form";
    }
  }

  // EDITAR (desde modal)
  @PostMapping("/{id}")
  public String editarRolDesdeModal(
      @PathVariable Integer id,
      @Valid @ModelAttribute("form") RolEditForm data,
      BindingResult br,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String q,
      Model model,
      HttpServletResponse resp) {

    if (!id.equals(data.getIdRol())) {
      br.reject("idMismatch", "El ID de la URL no coincide con el del formulario.");
    }
    if (br.hasErrors()) {
      model.addAttribute("mode", "edit");
      resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
      return "roles/_form :: form";
    }

    try {
      rolService.actualizarRol(data);
      String tabla = renderTabla(page, size, q, model);
      resp.setHeader("HX-Trigger", "{\"rolSaved\":{\"message\":\"Rol actualizado correctamente.\"}}");
      return tabla;
    } catch (Exception e) {
      model.addAttribute("mode", "edit");
      model.addAttribute("errorMessage", "Error al actualizar el rol: " + e.getMessage());
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      resp.setHeader("HX-Trigger", "{\"rolError\":{\"message\":\"No se pudo actualizar el rol.\"}}");
      return "roles/_form :: form";
    }
  }
}
