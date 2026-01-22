package pe.com.mesadepartes.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import pe.com.mesadepartes.dtos.usuarios.UsuarioCreateForm;
import pe.com.mesadepartes.dtos.usuarios.UsuarioEditForm;
import pe.com.mesadepartes.dtos.usuarios.UsuarioListItem;
import pe.com.mesadepartes.entity.Rol;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.service.FileStorageService;
import pe.com.mesadepartes.service.FileStorageService.FileStorageException;
import pe.com.mesadepartes.service.RolService;
import pe.com.mesadepartes.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final FileStorageService fileStorageService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService,
            FileStorageService fileStorageService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.fileStorageService = fileStorageService;
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
        Page<UsuarioListItem> usuariosPage = usuarioService.buscarUsuarios(q, pageable);

        if (page > 0 && usuariosPage.getContent().isEmpty() && usuariosPage.getTotalElements() > 0) {
            page = page - 1;
            pageable = PageRequest.of(page, size);
            usuariosPage = usuarioService.buscarUsuarios(q, pageable);
        }

        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("currentPage", usuariosPage.getNumber());
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("size", size);
        model.addAttribute("activeSection", "usuarios");
        return "usuarios/list";
    }

    @GetMapping
    public String listarInicial(Model model) {
        int page = 0;
        int size = 5;
        String q = null;

        Pageable pageable = PageRequest.of(page, size);
        Page<UsuarioListItem> usuariosPage = usuarioService.buscarUsuarios(q, pageable);

        // Convertir a la estructura que necesita el template
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("currentPage", usuariosPage.getNumber());
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("size", size);
        model.addAttribute("activeSection", "usuarios");
        return "usuarios/list";
    }

    @PostMapping
    public String listarParcial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String q,
            Model model) {
        return renderTabla(page, size, q, model);
    }

    @GetMapping("/editar/{id}")
    public String cargarUsuarioPorId(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Usuario usuario = usuarioService.cargarUsuario(id);
        if (usuario == null) {
            ra.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        if (!model.containsAttribute("form")) {
            UsuarioEditForm formUsuario = new UsuarioEditForm();
            formUsuario.setIdUsuario(usuario.getIdUsuario());
            formUsuario.setIdPersona(usuario.getPersona().getIdPersona());

            // Persona
            formUsuario.setApellidoPaterno(usuario.getPersona().getApellidoPaterno());
            formUsuario.setApellidoMaterno(usuario.getPersona().getApellidoMaterno());
            formUsuario.setNombres(usuario.getPersona().getNombres());
            formUsuario.setTipoDocumentoIdentidad(usuario.getPersona().getTipoDocumentoIdentidad());
            formUsuario.setNumeroDocumento(usuario.getPersona().getNumeroDocumento());
            formUsuario.setCorreoElectronico(usuario.getPersona().getCorreoElectronico());
            formUsuario.setTelefonoPersonal(usuario.getPersona().getTelefonoPersonal());
            formUsuario.setSexo(usuario.getPersona().getSexo());

            // Usuario
            formUsuario.setNombreUsuario(usuario.getNombreUsuario());
            formUsuario.setClave("");

            // Roles actuales
            formUsuario.setRolesIds(usuarioService.rolesIdsDeUsuario(usuario.getIdUsuario()));

            model.addAttribute("form", formUsuario);
        }
        model.addAttribute("isEdit", true);
        model.addAttribute("fotoUrl", usuario.getPersona().getRutaFoto());
        List<Rol> roles = rolService.listarActivos();
        model.addAttribute("roles", roles);
        model.addAttribute("activeSection", "usuarios");
        return "usuarios/form";
    }

    @PostMapping("/editar/{id}")
    public String editarUsuario(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") UsuarioEditForm form,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            BindingResult bindingResult,
            Model model) {

        if (!id.equals(form.getIdUsuario())) {
            bindingResult.reject("idMismatch", "El ID de la URL no coincide con el del formulario.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("toastError", "Revisa los campos marcados.");
            return "usuarios/form";
        }
        try {
            // String nuevaRuta = (foto != null && !foto.isEmpty()) ?
            // fileStorageService.guardarFotoPerfil(foto) : null;
            String nuevaRuta = null;
            usuarioService.actualizarUsuario(form, nuevaRuta);

            Usuario usuarioRefrescado = usuarioService.cargarUsuario(id);
            String fotoActual = usuarioRefrescado.getPersona().getRutaFoto();

            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("fotoUrl", fotoActual);
            model.addAttribute("toastSuccess", "Usuario actualizado correctamente.");

            return "redirect:/usuarios/editar/" + id;
        } catch (FileStorageException fse) {
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("fotoError", fse.getMessage());
            model.addAttribute("toastError", "La imagen no pudo guardarse.");
            return "usuarios/form";
        } catch (Exception ex) {
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("toastError", "No se pudo actualizar el usuario: " + ex.getMessage());
            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/activar")
    public String toggleActivo(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String q,
            Model model) {
        usuarioService.toggleActivo(id);
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
            usuarioService.eliminar(id);
            model.addAttribute("toastSuccess", "Usuario eliminado correctamente.");
        } catch (DataIntegrityViolationException ex) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            model.addAttribute("toastError",
                    "No se puede eliminar: el usuario tiene roles u otras referencias.");
        }
        return renderTabla(page, size, q, model);
    }

    @GetMapping("/crear")
    public String crearListaRolForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UsuarioCreateForm());
        }
        model.addAttribute("isEdit", false);
        List<Rol> roles = rolService.listarActivos();
        model.addAttribute("roles", roles);
        model.addAttribute("activeSection", "usuarios");
        return "usuarios/form";
    }

    @PostMapping("/crear")
    public String crearUsuario(
            @Valid @ModelAttribute("form") UsuarioCreateForm form,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("toastError", "Revisa los campos marcados.");
            return "usuarios/form";
        }

        try {
            String rutaFoto = fileStorageService.guardarFoto(foto);
            Integer idNuevo = usuarioService.crearUsuario(form, rutaFoto);

            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("toastSuccess", "Usuario creado correctamente (ID " + idNuevo + ").");
            model.addAttribute("form", new UsuarioCreateForm());
            model.addAttribute("shouldResetForm", true);
            return "usuarios/form";

        } catch (FileStorageException fse) {
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("fotoError", fse.getMessage());
            model.addAttribute("toastError", "La imagen no pudo guardarse.");
            return "usuarios/form";
        } catch (Exception ex) {
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeSection", "usuarios");
            model.addAttribute("toastError", "No se pudo crear el usuario: " + ex.getMessage());
            return "usuarios/form";
        }
    }
}
