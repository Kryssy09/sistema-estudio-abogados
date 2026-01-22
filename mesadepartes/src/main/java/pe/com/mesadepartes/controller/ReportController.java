package pe.com.mesadepartes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.service.ReportService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ExpedienteRepository expedienteRepository;

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /*
     * ===========================
     * GENERAR PDF DE EXPEDIENTES
     * ===========================
     */
    @GetMapping("/pdf/expedientes")
    public ResponseEntity<byte[]> generarPdfExpedientes() {
        try {
            byte[] pdf = reportService.generarPdfExpedientes();

            String filename = "reporte_expedientes_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /*
     * ===========================
     * GENERAR PDF DE USUARIOS
     * ===========================
     */
    @GetMapping("/pdf/usuarios")
    public ResponseEntity<byte[]> generarPdfUsuarios() {
        try {
            byte[] pdf = reportService.generarPdfUsuarios();

            String filename = "reporte_usuarios_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /*
     * ===========================
     * GENERAR PDF DE TIEMPOS (placeholder)
     * ===========================
     */
    @GetMapping("/pdf/tiempos")
    public ResponseEntity<byte[]> generarPdfTiempos() {
        // Por ahora retorna el mismo que expedientes
        // Puedes implementar lógica específica de tiempos después
        return generarPdfExpedientes();
    }

    /*
     * ===========================
     * EXPORTAR EXPEDIENTES A EXCEL
     * ===========================
     */
    @GetMapping("/excel/expedientes")
    public ResponseEntity<byte[]> exportarExpedientesExcel() {
        try {
            byte[] excel = reportService.exportarExpedientesExcel();

            String filename = "expedientes_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /*
     * ===========================
     * OBTENER ESTADÍSTICAS PARA GRÁFICA
     * ===========================
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            List<Expediente> expedientes = expedienteRepository.findAll();

            // Contar por tipo
            java.util.Map<String, Long> porTipo = expedientes.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            exp -> exp.getTipoExpediente() != null ? exp.getTipoExpediente() : "Sin tipo",
                            java.util.stream.Collectors.counting()));

            return ResponseEntity.ok(porTipo);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Estadísticas por estado de expediente
    @GetMapping("/estadisticas/estado")
    public ResponseEntity<java.util.Map<String, Long>> obtenerEstadisticasEstado() {
        try {
            java.util.Map<String, Long> porEstado = new java.util.HashMap<>();
            // Assuming possible estados are known; we query repository for each
            // You can also fetch distinct states dynamically if needed
            // For simplicity, we count for each known state
            String[] estados = { "ACTIVO", "ASIGNADO", "EN ATENCIÓN", "CERRADO" };
            for (String e : estados) {
                long count = expedienteRepository.countByEstadoExpediente(e);
                porEstado.put(e, count);
            }
            return ResponseEntity.ok(porEstado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
