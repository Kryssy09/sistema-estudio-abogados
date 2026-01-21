package pe.com.mesadepartes.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import pe.com.mesadepartes.dto.SolicitanteRowDto;
import pe.com.mesadepartes.entity.Persona;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.service.SolicitanteService;

import java.util.List;

/**
 * Controlador para gestionar operaciones de Solicitante:
 * - Página principal (Thymeleaf)
 * - Registro de solicitantes
 * - Listado completo y para Dashboard (DTO plano)
 * - Generación de PDF individual
 */
@Controller
@RequestMapping("/solicitantes")
@RequiredArgsConstructor
public class SolicitantesController {

    private final SolicitanteService solicitanteService;

    // ============================================================
    // 1️⃣ Página principal de Solicitantes (Thymeleaf)
    // ============================================================
    @GetMapping
    public String paginaSolicitantes(Model model) {
        model.addAttribute("pageTitle", "Solicitantes");
        return "pages/solicitantes";
    }

    // ============================================================
    // 2️⃣ Registrar nuevo solicitante
    // ============================================================
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<Solicitante> crear(@RequestBody Persona personaPayload) {
        // En producción puedes obtener idUsuarioCreador desde seguridad
        Solicitante guardado = solicitanteService.guardarPersona(personaPayload, 1);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // ============================================================
    // 3️⃣ Listar solicitantes (entidad completa)
    // ============================================================
    @GetMapping("/listar-todos")
    @ResponseBody
    public ResponseEntity<List<Solicitante>> listarSolicitantes() {
        System.out.println("Listar solicitantes called");
        List<Solicitante> lista = solicitanteService.listarSolicitantes();
        System.out.println("Solicitantes found: " + lista.size());
        return ResponseEntity.ok(lista);
    }

    // ============================================================
    // 4️⃣ Listar solicitantes (DTO plano para Dashboard)
    // ============================================================
    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<SolicitanteRowDto>> listarSolicitantesDashboard() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        List<SolicitanteRowDto> body = solicitanteService.listarFilas();
        return ResponseEntity.ok().headers(headers).body(body);
    }

    // ============================================================
    // 5️⃣ Descargar PDF individual del solicitante
    // ============================================================
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer id) {
        byte[] pdf = solicitanteService.generarPdfSolicitante(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=solicitante_" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    // ============================================================
    // 6️⃣ Buscar solicitantes (para autocompletado/modal)
    // ============================================================
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<Solicitante>> buscarSolicitantes(@RequestParam String q) {
        // Reutilizamos la lógica de paginación pero pedimos la primera página con 20
        // resultados
        // para no sobrecargar la vista
        var page = solicitanteService.listarSolicitantesPaginado(0, 20, q);
        return ResponseEntity.ok(page.getContent());
    }

    // ============================================================
    // 7️⃣ Obtener solicitante por ID
    // ============================================================
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Solicitante> obtenerSolicitante(@PathVariable Integer id) {
        return solicitanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================================================
    // 8️⃣ Eliminar solicitante
    // ============================================================
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarSolicitante(@PathVariable Integer id) {
        try {
            solicitanteService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el solicitante porque tiene expedientes asociados");
        }
    }

    // ============================================================
    // 9️⃣ Obtener cantidad de expedientes de un solicitante
    // ============================================================
    @GetMapping("/{id}/expedientes/count")
    @ResponseBody
    public ResponseEntity<Long> contarExpedientes(@PathVariable Integer id) {
        Long count = solicitanteService.contarExpedientes(id);
        return ResponseEntity.ok(count);
    }
}