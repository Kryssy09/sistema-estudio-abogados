// ExpedienteController.java
package pe.com.mesadepartes.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import pe.com.mesadepartes.dtos.expediente.CreateArchivoSesion;
import pe.com.mesadepartes.dtos.expediente.EditArchivoSesion;
import pe.com.mesadepartes.dtos.expediente.DocItem;
import pe.com.mesadepartes.dtos.expediente.ExpedienteForm;
import pe.com.mesadepartes.dtos.sesion.SesionListItem;
import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.entity.ExpedienteSesion;
import pe.com.mesadepartes.entity.ExpedienteSesionArchivo;
import pe.com.mesadepartes.entity.Invitado;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.service.ExpedienteSesionArchivoService;
import pe.com.mesadepartes.service.FileStorageService;
import pe.com.mesadepartes.service.InvitadoService;
import pe.com.mesadepartes.service.SesionService;
import pe.com.mesadepartes.service.SolicitanteService;
import pe.com.mesadepartes.service.UsuarioService;

@Controller
@RequestMapping("/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private static final Logger logger = Logger.getLogger(ExpedienteController.class.getName());

    private final ExpedienteRepository expedienteRepository;
    private final SolicitanteService solicitanteService;
    private final FileStorageService fileStorageService;
    private final InvitadoService invitadoService;
    private final UsuarioService usuarioService;
    private final SesionService sesionService;
    private final ExpedienteSesionArchivoService expedienteSesionArchivoService;

    private static final Map<String, String> ESTADO_LABELS = Map.of(
            "SIN_ASIG", "Sin Asignar",
            "ASIG", "Asignado",
            "EN_ATE", "En Atención",
            "CERR", "Cerrado",
            "ANUL", "Anulado");

    @GetMapping
    public String listarExpedientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        logger.info("=== ACCEDIENDO A /expedientes ===");
        logger.info("Página: " + page + ", Tamaño: " + size + ", Búsqueda: " + search);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
            Page<Expediente> pageExpedientes;

            search = (search == null ? "" : search);

            // if (search != null && !search.trim().isEmpty()) {
            logger.info("Buscando expedientes con término: " + search);
            pageExpedientes = expedienteRepository.buscarExpedientes(search.trim(), pageable);
            /*
             * } else {
             * logger.info("Listando todos los expedientes");
             * pageExpedientes = expedienteRepository.findAll(pageable);
             * }
             */

            logger.info("Expedientes encontrados: " + pageExpedientes.getTotalElements());

            model.addAttribute("expedientes", pageExpedientes.getContent());
            model.addAttribute("currentPage", pageExpedientes.getNumber());
            model.addAttribute("totalPages", pageExpedientes.getTotalPages());
            model.addAttribute("totalItems", pageExpedientes.getTotalElements());

            return "expedientes/list";

        } catch (Exception e) {
            logger.severe("Error al listar expedientes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los expedientes");
            return "error";
        }
    }

    @GetMapping("/crear")
    public String crearExpedienteForm(Model model) {
        List<Solicitante> solicitantes = solicitanteService.listarActivos();

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ExpedienteForm());
        }

        model.addAttribute("isEdit", false);
        model.addAttribute("solicitantes", solicitantes);
        model.addAttribute("codigoExpediente", generarCodigo());
        return "expedientes/form";
    }

    @PostMapping("/crear")
    public String crearExpediente(
            @ModelAttribute("form") ExpedienteForm form,
            @RequestParam(name = "formatoArchivo", required = false) MultipartFile formatoArchivo,
            Model model,
            RedirectAttributes redirectAttributes) throws Exception {

        // Validación básica
        if (form.getSolicitanteId() == null) {
            model.addAttribute("errorMessage", "Debe seleccionar un solicitante");
            List<Solicitante> solicitantes = solicitanteService.listarActivos();
            model.addAttribute("isEdit", false);
            model.addAttribute("solicitantes", solicitantes);
            model.addAttribute("codigoExpediente", generarCodigo());
            return "expedientes/form";
        }

        // 1) Generar código y persistir el expediente (sin archivo todavía)
        String codigo = generarCodigo();
        String ESTADO_ACTIVO = "ACT";

        Expediente expediente = new Expediente();

        if (formatoArchivo != null && !formatoArchivo.isEmpty()) {
            String rutaRelativa = fileStorageService.guardarSolicitudExpediente(formatoArchivo);
            expediente.setRutaArchivoFormatoSolicitud(rutaRelativa);
        }

        expediente.setEspecialidad(form.getEspecialidadId());
        expediente.setEstadoRegistro(ESTADO_ACTIVO);
        expediente.setPersonaSolicitante(solicitanteService.buscarPorId(form.getSolicitanteId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitante no encontrado")));
        expediente.setCodigoSeguimiento(codigo);
        expediente.setIdUsuarioCreador(1);
        expediente.setMutuoAcuerdo(form.getMutuoAcuerdo());
        expediente.setTipoExpediente(form.getTipoExpediente());
        expediente.setEstadoExpediente("SIN_ASIG"); // Estado inicial
        expediente.setReseniaSolicitud(form.getReseniaSolicitud());
        expediente.setFechaCreacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        expedienteRepository.save(expediente);

        redirectAttributes.addFlashAttribute("toastSuccess", "Expediente creado correctamente");
        return "redirect:/expedientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes ra) {

        Optional<Expediente> opt = expedienteRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("toastError", "Expediente no encontrado.");
            return "redirect:/expedientes";
        }
        Expediente exp = opt.get();

        // Listas para campos editables
        model.addAttribute("invitados", invitadoService.listarActivos());
        model.addAttribute("conciliadores", usuarioService.listarTodos());
        model.addAttribute("expedientes", expedienteRepository.findAll());

        // Cargar las sesiones del expediente
        List<SesionListItem> sesiones = sesionService.findSesionesByExpediente(exp.getIdExpediente());
        model.addAttribute("sesiones", sesiones);

        // Preparar formulario para edición
        if (!model.containsAttribute("form")) {
            ExpedienteForm form = new ExpedienteForm();
            form.setIdExpediente(exp.getIdExpediente());
            form.setTipoExpediente(exp.getTipoExpediente());
            form.setMutuoAcuerdo(exp.getMutuoAcuerdo());
            form.setEspecialidadId(exp.getEspecialidad());
            form.setReseniaSolicitud(exp.getReseniaSolicitud());
            form.setInvitadoId(exp.getPersonaInvitada() != null ? exp.getPersonaInvitada().getIdInvitado() : null);
            form.setIdUsuarioAsignado(
                    exp.getUsuarioAsignado() != null ? exp.getUsuarioAsignado().getIdUsuario() : null);
            form.setIdExpedienteOrigen(exp.getIdExpedienteOrigen());

            model.addAttribute("form", form);
        }

        model.addAttribute("isEdit", true);
        model.addAttribute("expediente", exp);
        model.addAttribute("estadoLabel",
                ESTADO_LABELS.getOrDefault(exp.getEstadoExpediente(), exp.getEstadoExpediente()));

        return "expedientes/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(
            @PathVariable Integer id,
            @ModelAttribute("form") ExpedienteForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación básica
        if (form.getEspecialidadId() == null) {
            model.addAttribute("errorMessage", "Debe seleccionar una especialidad");
            // Cargar datos necesarios para el formulario
            Optional<Expediente> expOpt = expedienteRepository.findById(id);
            if (expOpt.isPresent()) {
                Expediente exp = expOpt.get();
                model.addAttribute("invitados", invitadoService.listarActivos());
                model.addAttribute("conciliadores", usuarioService.listarTodos());
                model.addAttribute("expedientes", expedienteRepository.findAll());

                // Simplificado: no cargar sesiones para evitar errores
                model.addAttribute("sesiones", java.util.Collections.emptyList());

                model.addAttribute("isEdit", true);
                model.addAttribute("expediente", exp);
                model.addAttribute("estadoLabel",
                        ESTADO_LABELS.getOrDefault(exp.getEstadoExpediente(), exp.getEstadoExpediente()));
            }
            return "expedientes/form";
        }

        Expediente exp = expedienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe expediente " + id));

        // Solo los campos habilitados:
        exp.setTipoExpediente(form.getTipoExpediente());
        exp.setMutuoAcuerdo(form.getMutuoAcuerdo());
        exp.setEspecialidad(form.getEspecialidadId());
        exp.setReseniaSolicitud(form.getReseniaSolicitud());

        if (form.getInvitadoId() != null) {
            Invitado inv = invitadoService.buscarPorId(form.getInvitadoId());
            exp.setPersonaInvitada(inv);
        } else {
            exp.setPersonaInvitada(null);
        }

        if (form.getIdUsuarioAsignado() != null) {
            Usuario conciliador = usuarioService.buscarPorId(form.getIdUsuarioAsignado())
                    .orElseThrow(() -> new IllegalArgumentException("Conciliador no válido"));
            if (exp.getUsuarioAsignado() == null || exp.getFechaAsignacion() == null
                    || !exp.getUsuarioAsignado().getIdUsuario().equals(conciliador.getIdUsuario())) {
                exp.setFechaAsignacion(new Date()); // ahora
            }
            exp.setUsuarioAsignado(conciliador);
        } else {
            exp.setUsuarioAsignado(null);
        }

        if ("PL".equalsIgnoreCase(form.getTipoExpediente())) {
            exp.setIdExpedienteOrigen(form.getIdExpedienteOrigen());
        } else {
            exp.setIdExpedienteOrigen(null);
        }

        expedienteRepository.save(exp);
        redirectAttributes.addFlashAttribute("toastSuccess", "Cambios guardados correctamente.");

        return "redirect:/expedientes/editar/" + id;
    }

    @GetMapping("/{id}/sesiones/nueva")
    public String nuevaSesionForm(@PathVariable Integer id, Model model) {
        Expediente expediente = expedienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe expediente " + id));

        model.addAttribute("expediente", expediente);
        model.addAttribute("sesionForm", new CreateArchivoSesion());

        return "expedientes/sesion-form";
    }

    @PostMapping("/{id}/sesiones")
    public String crearSesion(
            @PathVariable Integer id,
            @ModelAttribute CreateArchivoSesion form, // @RequestParam("docs[*].file")
            RedirectAttributes ra) throws Exception {

        logger.info("Creando nueva sesión para el expediente: " + id);
        logger.info("Estado de la sesión: " + form.getEstadoSesion());

        Expediente expediente = expedienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe expediente " + id));
        ExpedienteSesion sesion = new ExpedienteSesion();
        sesion.setExpediente(expediente);

        String estado = form.getEstadoSesion();
        String estadoShort = estado;
        if ("Programada".equals(estado)) {
            estadoShort = "Progra";
            sesion.setFechaSesion(form.getFechaSesion());
            sesion.setDetallesSesion(form.getDetallesSesion());
        } else if ("En Curso".equals(estado)) {
            estadoShort = "En Cur";
            sesion.setFechaSesion(LocalDateTime.now());
            sesion.setDetallesSesion("Sesión en curso"); // Default value
        }
        sesion.setEstadoSesion(estadoShort);

        sesion.setResolucionSesion(form.getResolucionSesion());
        sesion.setEstadoRegistro("ACT");
        sesion.setFechaCreacion(LocalDateTime.now());
        sesion.setIdUsuarioCreador(1);

        Integer maxSecuencia = sesionService.findMaxSecuenciaByExpediente(id);
        sesion.setSecuencia(maxSecuencia + 1);

        ExpedienteSesion nuevaSesion = sesionService.crearSesion(sesion);
        logger.info("Nueva sesión creada con ID: " + nuevaSesion.getIdExpedienteSesion());

        if ("En Curso".equals(estado)) {
            logger.info("Redirigiendo a la página de edición de la sesión: " + nuevaSesion.getIdExpedienteSesion());
            return "redirect:/expedientes/sesiones/" + nuevaSesion.getIdExpedienteSesion() + "/editar";
        }

        ra.addFlashAttribute("toastSuccess", "Sesión creada.");
        logger.info("Redirigiendo a la página de edición del expediente: " + id);
        return "redirect:/expedientes/editar/" + id;
    }

    @GetMapping("/sesiones/{sesionId}/editar")
    public String editarSesionForm(@PathVariable Integer sesionId, Model model) {
        ExpedienteSesion sesion = sesionService.buscarPorId(sesionId);
        if (sesion == null) {
            // Manejar el caso en que la sesión no se encuentra
            return "redirect:/error"; // O a una página de error personalizada
        }

        // Acceder al expediente antes de que se cierre la transacción
        Expediente expediente = sesion.getExpediente();
        if (expediente == null) {
            return "redirect:/error";
        }

        // Forzar la inicialización de las propiedades lazy si es necesario
        String codigoSeguimiento = expediente.getCodigoSeguimiento();
        Integer idExpediente = expediente.getIdExpediente();

        model.addAttribute("expediente", expediente);
        model.addAttribute("sesion", sesion);

        return "expedientes/sesion-edit-form";
    }

    @PostMapping("/sesiones/{sesionId}/editar")
    public String actualizarSesion(
            @PathVariable Integer sesionId,
            @ModelAttribute("sesion") EditArchivoSesion form,
            RedirectAttributes ra) {

        form.setIdExpedienteSesion(sesionId);
        ExpedienteSesion sesionActualizada = sesionService.actualizarSesion(form);

        ra.addFlashAttribute("toastSuccess", "Sesión actualizada correctamente.");
        return "redirect:/expedientes/editar/" + sesionActualizada.getExpediente().getIdExpediente();
    }

    @DeleteMapping("/sesion/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteSesion(@PathVariable Integer id) {
        try {
            sesionService.deleteById(id);
            return new ResponseEntity<>(Map.of("message", "Sesión eliminada correctamente"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generarCodigo() {
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        java.util.Random r = new java.util.Random();
        String pref = "" + letras.charAt(r.nextInt(letras.length()))
                + "-" + letras.charAt(r.nextInt(letras.length()))
                + "-" + letras.charAt(r.nextInt(letras.length()));
        String nro = String.format("%05d", r.nextInt(99999) + 1);
        return pref + "-" + nro;
    }
}
