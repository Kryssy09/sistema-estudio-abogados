// ExpedienteController.java
package pe.com.mesadepartes.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import lombok.RequiredArgsConstructor;
import pe.com.mesadepartes.dtos.expediente.CreateArchivoSesion;
import pe.com.mesadepartes.dtos.expediente.EditArchivoSesion;
import pe.com.mesadepartes.dtos.expediente.DocItem;
import pe.com.mesadepartes.dtos.expediente.ExpedienteForm;
import pe.com.mesadepartes.dtos.sesion.SesionListItem;
import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.entity.ExpedienteSesion;
import pe.com.mesadepartes.entity.ExpedienteSesionArchivo;
import pe.com.mesadepartes.entity.ExpedienteMovimiento;
import pe.com.mesadepartes.entity.Invitado;
import org.springframework.http.ResponseEntity;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.service.ExpedienteSesionArchivoService;
import pe.com.mesadepartes.service.FileStorageService;
import pe.com.mesadepartes.service.InvitadoService;
import pe.com.mesadepartes.service.SesionService;
import pe.com.mesadepartes.service.SolicitanteService;
import pe.com.mesadepartes.service.UsuarioService;
import pe.com.mesadepartes.service.AreaService;
import pe.com.mesadepartes.service.ExpedienteMovimientoService;
import pe.com.mesadepartes.service.ReportService;
import pe.com.mesadepartes.service.EmailService;

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
    private final AreaService areaService;
    private final ExpedienteMovimientoService expedienteMovimientoService;
    private final ReportService reportService;
    private final EmailService emailService;

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
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idArea,
            @RequestParam(required = false) Integer idEspecialista,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        logger.info("=== ACCEDIENDO A /expedientes ===");
        logger.info("Página: " + page + ", Tamaño: " + size + ", Búsqueda: " + search);

        try {
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Expediente> pageExpedientes;

            search = (search == null ? "" : search);

            logger.info("Buscando expedientes con término: " + search);
            Specification<Expediente> spec = buildSpecification(search.trim(), estado, idArea, idEspecialista,
                    fechaDesde, fechaHasta);
            pageExpedientes = expedienteRepository.findAll(spec, pageable);

            logger.info("Expedientes encontrados: " + pageExpedientes.getTotalElements());

            model.addAttribute("areas", areaService.listarAreas());
            model.addAttribute("especialistas", usuarioService.listarTodos());
            model.addAttribute("paramEstado", estado);
            model.addAttribute("paramArea", idArea);
            model.addAttribute("paramEspecialista", idEspecialista);
            model.addAttribute("paramFechaDesde", fechaDesde);
            model.addAttribute("paramFechaHasta", fechaHasta);

            model.addAttribute("expedientes", pageExpedientes.getContent());
            model.addAttribute("currentPage", pageExpedientes.getNumber());
            model.addAttribute("totalPages", pageExpedientes.getTotalPages());
            model.addAttribute("totalItems", pageExpedientes.getTotalElements());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

            return "expedientes/list";

        } catch (Exception e) {
            logger.severe("Error al listar expedientes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los expedientes");
            return "error";
        }
    }

    @GetMapping("/fragmento-tabla")
    public String obtenerFragmentoTabla(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idArea,
            @RequestParam(required = false) Integer idEspecialista,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        try {
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Expediente> pageExpedientes;

            search = (search == null ? "" : search);
            Specification<Expediente> spec = buildSpecification(search.trim(), estado, idArea, idEspecialista,
                    fechaDesde, fechaHasta);
            pageExpedientes = expedienteRepository.findAll(spec, pageable);

            model.addAttribute("expedientes", pageExpedientes.getContent());
            model.addAttribute("currentPage", pageExpedientes.getNumber());
            model.addAttribute("totalPages", pageExpedientes.getTotalPages());
            model.addAttribute("totalItems", pageExpedientes.getTotalElements());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

            return "expedientes/list :: tabla-expedientes";

        } catch (Exception e) {
            logger.severe("Error al cargar fragmento de expedientes: " + e.getMessage());
            return "error";
        }
    }

    private Specification<Expediente> buildSpecification(String search, String estado, Integer idArea,
            Integer idEspecialista, Date fechaDesde, Date fechaHasta) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("estadoRegistro"), "ACT"));

            jakarta.persistence.criteria.Join<Object, Object> solicitanteJoin = root.join("personaSolicitante",
                    jakarta.persistence.criteria.JoinType.LEFT);
            jakarta.persistence.criteria.Join<Object, Object> personaJoin = solicitanteJoin.join("persona",
                    jakarta.persistence.criteria.JoinType.LEFT);

            jakarta.persistence.criteria.Join<Object, Object> usuarioJoin = root.join("usuarioAsignado",
                    jakarta.persistence.criteria.JoinType.LEFT);

            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                Predicate code = cb.like(cb.lower(root.get("codigoSeguimiento")), likePattern);
                Predicate type = cb.like(cb.lower(root.get("tipoExpediente")), likePattern);
                Predicate est = cb.like(cb.lower(root.get("estadoExpediente")), likePattern);
                Predicate res = cb.like(cb.lower(root.get("reseniaSolicitud")), likePattern);
                Predicate name = cb.like(cb.lower(personaJoin.get("nombres")), likePattern);
                Predicate ape = cb.like(cb.lower(personaJoin.get("apellidoPaterno")), likePattern);
                Predicate doc = cb.like(cb.lower(personaJoin.get("numeroDocumento")), likePattern);
                predicates.add(cb.or(code, type, est, res, name, ape, doc));
            }

            if (estado != null && !estado.isEmpty()) {
                predicates.add(cb.equal(root.get("estadoExpediente"), estado));
            }

            if (idEspecialista != null) {
                predicates.add(cb.equal(usuarioJoin.get("idUsuario"), idEspecialista));
            }

            if (idArea != null) {
                jakarta.persistence.criteria.Join<Object, Object> areaJoin = usuarioJoin.join("idArea",
                        jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.equal(areaJoin.get("idArea"), idArea));
            }

            if (fechaDesde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaDesde));
            }

            if (fechaHasta != null) {
                // Add one day to fechaHasta to include the whole day
                Date endOfDay = new Date(fechaHasta.getTime() + (1000 * 60 * 60 * 24));
                predicates.add(cb.lessThan(root.get("fechaCreacion"), endOfDay));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
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

        if (form.getEspecialidadId() == null) {
            model.addAttribute("errorMessage", "Debe seleccionar una especialidad");
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
        } else {
            expediente.setRutaArchivoFormatoSolicitud("");
        }

        expediente.setEspecialidad(form.getEspecialidadId());
        expediente.setEstadoRegistro(ESTADO_ACTIVO);
        expediente.setPersonaSolicitante(solicitanteService.buscarPorId(form.getSolicitanteId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitante no encontrado")));
        expediente.setCodigoSeguimiento(codigo);
        expediente.setIdUsuarioCreador(1);
        expediente.setMutuoAcuerdo(form.getMutuoAcuerdo() != null ? form.getMutuoAcuerdo() : false);
        expediente.setTipoExpediente(form.getTipoExpediente());
        expediente.setEstadoExpediente("SIN_ASIG"); // Estado inicial
        expediente.setReseniaSolicitud(form.getReseniaSolicitud());
        expediente.setPrioridad(form.getPrioridad() != null ? form.getPrioridad() : "Media"); // Default Media
        expediente.setFechaCreacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        try {
            Expediente savedExpediente = expedienteRepository.save(expediente);

            // Generar y guardar el PDF del Cargo de Recepción
            try {
                byte[] pdfCargo = reportService.generarPdfCargo(savedExpediente);
                String rutaCargo = fileStorageService.guardarCargoExpediente(savedExpediente.getCodigoSeguimiento(),
                        pdfCargo);
                savedExpediente.setRutaCargoRecepcion(rutaCargo);
                expedienteRepository.save(savedExpediente);
            } catch (Exception e) {
                logger.severe("Error al generar o guardar el cargo de recepción: " + e.getMessage());
                // No bloquea la creación del expediente en sí
            }

            Usuario creador = usuarioService.buscarPorId(1).orElse(null);
            expedienteMovimientoService.registrarMovimiento(
                    savedExpediente,
                    creador,
                    "CREACIÓN",
                    null,
                    savedExpediente.getEstadoExpediente(),
                    null,
                    null,
                    "Expediente registrado en el sistema.");

            redirectAttributes.addFlashAttribute("toastSuccess", "Expediente creado correctamente");
            return "redirect:/expedientes";
        } catch (Exception e) {
            logger.severe("Error general al crear expediente: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage",
                    "Error al crear el expediente. Verifique que todos los datos sean correctos. " + e.getMessage());
            List<Solicitante> solicitantes = solicitanteService.listarActivos();
            model.addAttribute("isEdit", false);
            model.addAttribute("solicitantes", solicitantes);
            model.addAttribute("codigoExpediente", codigo);
            return "expedientes/form";
        }
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
        model.addAttribute("abogados", usuarioService.listarTodos());
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
            form.setPrioridad(exp.getPrioridad());
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

    /**
     * Asignar abogado directamente desde el formulario de edición.
     * Usa POST normal (no AJAX) con redirect de vuelta al formulario.
     */
    @PostMapping("/{id}/asignar")
    public String asignarAbogado(
            @PathVariable Integer id,
            @RequestParam("idAbogado") Integer idAbogado,
            @RequestParam(value = "comentario", required = false, defaultValue = "") String comentario,
            RedirectAttributes ra) {
        try {
            Expediente exp = expedienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Expediente no encontrado"));

            if ("CERR".equals(exp.getEstadoExpediente())) {
                ra.addFlashAttribute("toastError", "No se puede modificar un expediente cerrado.");
                return "redirect:/expedientes/editar/" + id;
            }

            Usuario abogado = usuarioService.buscarPorId(idAbogado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idAbogado));

            String estadoAnterior = exp.getEstadoExpediente();
            Usuario asignadoAnterior = exp.getUsuarioAsignado();

            exp.setUsuarioAsignado(abogado);
            if (!"ASIG".equals(estadoAnterior) && !"EN_ATE".equals(estadoAnterior)) {
                exp.setEstadoExpediente("ASIG");
            }
            exp.setFechaAsignacion(new Date());
            exp.setFechaModificacion(new Date());
            exp.setIdUsuarioModificador(1);

            expedienteRepository.save(exp);

            Usuario actor = usuarioService.buscarPorId(1).orElse(abogado);
            String detalles = comentario.isBlank()
                    ? "Abogado asignado: " + abogado.getPersona().getNombres() + " "
                            + abogado.getPersona().getApellidoPaterno()
                    : comentario;
            expedienteMovimientoService.registrarMovimiento(
                    exp, actor, "DERIVACIÓN", estadoAnterior, exp.getEstadoExpediente(),
                    asignadoAnterior, abogado, detalles);

            ra.addFlashAttribute("toastSuccess",
                    "Expediente asignado a " + abogado.getPersona().getNombres()
                            + " " + abogado.getPersona().getApellidoPaterno() + " correctamente.");
        } catch (Exception e) {
            logger.severe("Error al asignar abogado: " + e.getMessage());
            ra.addFlashAttribute("toastError", "Error al asignar: " + e.getMessage());
        }
        return "redirect:/expedientes/editar/" + id;
    }

    @PostMapping("/editar/{id}")
    public String actualizar(
            @PathVariable Integer id,
            @ModelAttribute("form") ExpedienteForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        Expediente exp = expedienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe expediente " + id));

        // Validación: No permitir edición si el expediente está cerrado
        if ("CERR".equals(exp.getEstadoExpediente())) {
            redirectAttributes.addFlashAttribute("toastError", "No se puede editar un expediente cerrado.");
            return "redirect:/expedientes";
        }

        // Validación básica
        if (form.getEspecialidadId() == null) {
            model.addAttribute("errorMessage", "Debe seleccionar una especialidad");
            // Cargar datos necesarios para el formulario
            model.addAttribute("invitados", invitadoService.listarActivos());
            model.addAttribute("conciliadores", usuarioService.listarTodos());
            model.addAttribute("expedientes", expedienteRepository.findAll());

            // Simplificado: no cargar sesiones para evitar errores
            model.addAttribute("sesiones", java.util.Collections.emptyList());

            model.addAttribute("isEdit", true);
            model.addAttribute("expediente", exp);
            model.addAttribute("estadoLabel",
                    ESTADO_LABELS.getOrDefault(exp.getEstadoExpediente(), exp.getEstadoExpediente()));
            return "expedientes/form";
        }

        // Solo los campos habilitados:
        exp.setTipoExpediente(form.getTipoExpediente());
        exp.setMutuoAcuerdo(form.getMutuoAcuerdo());
        exp.setEspecialidad(form.getEspecialidadId());
        exp.setReseniaSolicitud(form.getReseniaSolicitud());
        exp.setPrioridad(form.getPrioridad());

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

    @PostMapping("/{id}/actualizar-estado")
    @ResponseBody
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Integer id,
            @RequestParam("estado") String nuevoEstado,
            @RequestParam(value = "idEspecialista", required = false) Integer idNuevoEspecialista,
            @RequestParam(value = "observaciones", required = false) String observaciones) {

        try {
            Expediente exp = expedienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Expediente no encontrado"));

            String estadoAnterior = exp.getEstadoExpediente();
            Usuario asignadoAnterior = exp.getUsuarioAsignado();

            String accion = "CAMBIO_ESTADO";
            Usuario nuevoAsignado = null;

            if (idNuevoEspecialista != null) {
                nuevoAsignado = usuarioService.buscarPorId(idNuevoEspecialista).orElse(null);
                if (asignadoAnterior == null || !asignadoAnterior.getIdUsuario().equals(idNuevoEspecialista)) {
                    accion = "REASIGNACIÓN";
                }
            } else {
                if (asignadoAnterior != null) {
                    accion = "REASIGNACIÓN"; // Se quitó el asignado
                }
            }

            exp.setEstadoExpediente(nuevoEstado);
            exp.setUsuarioAsignado(nuevoAsignado);
            exp.setFechaModificacion(
                    Date.from(java.time.LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            exp.setIdUsuarioModificador(1); // TODO: Replace with logged in user

            expedienteRepository.save(exp);

            Usuario actor = usuarioService.buscarPorId(1).orElse(null); // TODO: Replace with logged in user
            expedienteMovimientoService.registrarMovimiento(
                    exp, actor, accion, estadoAnterior, nuevoEstado,
                    asignadoAnterior, nuevoAsignado, observaciones);

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Estado actualizado correctamente\"}");
        } catch (Exception e) {
            logger.severe("Error al actualizar estado: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}/historial")
    public String verHistorial(@PathVariable Integer id, Model model) {
        List<ExpedienteMovimiento> movimientos = expedienteMovimientoService.obtenerHistorial(id);
        model.addAttribute("movimientos", movimientos);
        return "expedientes/list :: timeline-historial";
    }

    @GetMapping("/{id}/cargo/pdf")
    public ResponseEntity<byte[]> descargarCargoPdf(@PathVariable Integer id) {
        Optional<Expediente> expOpt = expedienteRepository.findById(id);
        if (expOpt.isPresent()) {
            Expediente exp = expOpt.get();
            if (exp.getRutaCargoRecepcion() != null && !exp.getRutaCargoRecepcion().isEmpty()) {
                byte[] pdfBytes = fileStorageService.leerArchivo(exp.getRutaCargoRecepcion());
                if (pdfBytes != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment",
                            "cargo_recepcion_" + exp.getCodigoSeguimiento() + ".pdf");
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(pdfBytes);
                }
            } else {
                // Generar dinámicamente si no existe pero el expediente sí (fallback)
                try {
                    byte[] pdfBytes = reportService.generarPdfCargo(exp);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment",
                            "cargo_recepcion_" + exp.getCodigoSeguimiento() + ".pdf");
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(pdfBytes);
                } catch (Exception e) {
                    logger.severe("Error al generar PDF dinámico: " + e.getMessage());
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/derivar")
    @ResponseBody
    public ResponseEntity<?> derivarExpediente(
            @PathVariable Integer id,
            @RequestParam("idAbogado") Integer idAbogado,
            @RequestParam("comentario") String comentario) {
        try {
            Expediente exp = expedienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Expediente no encontrado"));

            if ("CERR".equals(exp.getEstadoExpediente())) {
                return ResponseEntity.badRequest()
                        .body("{\"success\": false, \"message\": \"No se puede derivar un expediente cerrado.\"}");
            }

            if (comentario == null || comentario.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("{\"success\": false, \"message\": \"Debe ingresar un comentario de derivación.\"}");
            }

            Usuario abogado = usuarioService.buscarPorId(idAbogado)
                    .orElseThrow(() -> new RuntimeException("Abogado no encontrado"));

            String estadoAnterior = exp.getEstadoExpediente();
            Usuario asignadoAnterior = exp.getUsuarioAsignado();

            exp.setUsuarioAsignado(abogado);
            if (!"ASIG".equals(estadoAnterior) && !"EN_ATE".equals(estadoAnterior)) {
                exp.setEstadoExpediente("ASIG");
            }
            exp.setFechaAsignacion(new Date());
            exp.setFechaModificacion(
                    Date.from(java.time.LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            exp.setIdUsuarioModificador(1);

            expedienteRepository.save(exp);

            Usuario actor = usuarioService.buscarPorId(1).orElse(null);
            expedienteMovimientoService.registrarMovimiento(
                    exp, actor, "DERIVACIÓN", estadoAnterior, exp.getEstadoExpediente(),
                    asignadoAnterior, abogado, comentario);

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Expediente derivado correctamente a " +
                    abogado.getPersona().getNombres() + " " + abogado.getPersona().getApellidoPaterno() + "\"}");
        } catch (Exception e) {
            logger.severe("Error al derivar expediente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"Error al derivar: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/{id}/cerrar")
    @ResponseBody
    public ResponseEntity<?> cerrarExpediente(@PathVariable Integer id, @RequestParam("motivo") String motivo) {
        try {
            Expediente exp = expedienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Expediente no encontrado"));

            if ("CERR".equals(exp.getEstadoExpediente())) {
                return ResponseEntity.badRequest()
                        .body("{\"success\": false, \"message\": \"El expediente ya se encuentra cerrado.\"}");
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("{\"success\": false, \"message\": \"Debe ingresar un motivo de cierre.\"}");
            }

            String estadoAnterior = exp.getEstadoExpediente();

            exp.setEstadoExpediente("CERR");
            exp.setMotivoCierre(motivo);
            exp.setFechaModificacion(
                    Date.from(java.time.LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            exp.setIdUsuarioModificador(1); // TODO: Reemplazar con usuario autenticado

            expedienteRepository.save(exp);

            Usuario actor = usuarioService.buscarPorId(1).orElse(null);
            expedienteMovimientoService.registrarMovimiento(
                    exp, actor, "CIERRE FORMAL", estadoAnterior, "CERR",
                    exp.getUsuarioAsignado(), exp.getUsuarioAsignado(), motivo);

            // Intentar notificar por correo
            if (exp.getPersonaSolicitante() != null && exp.getPersonaSolicitante().getPersona() != null) {
                String email = exp.getPersonaSolicitante().getPersona().getCorreoElectronico();
                emailService.notificarCierreExpediente(email, exp.getCodigoSeguimiento(), motivo);
            }

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Expediente cerrado exitosamente.\"}");
        } catch (Exception e) {
            logger.severe("Error al cerrar expediente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "{\"success\": false, \"message\": \"Error al cerrar el expediente: " + e.getMessage() + "\"}");
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
