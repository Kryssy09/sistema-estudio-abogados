package pe.com.mesadepartes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.repository.UsuarioRepository;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /*
     * ===========================
     * GENERAR PDF DE EXPEDIENTES
     * ===========================
     */
    public byte[] generarPdfExpedientes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
            com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();

            // Título
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                    com.lowagie.text.Font.BOLD);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Reporte de Expedientes", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Fecha de generación
            com.lowagie.text.Font dateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.NORMAL, java.awt.Color.GRAY);
            com.lowagie.text.Paragraph date = new com.lowagie.text.Paragraph(
                    "Generado: " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
            date.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Tabla
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1f, 2f, 2f, 2f, 1.5f, 1.5f, 2f });

            // Encabezados
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.BOLD, java.awt.Color.WHITE);
            String[] headers = { "ID", "Solicitante", "Tipo", "Código", "Estado", "Fecha", "Reseña" };

            for (String header : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                        new com.lowagie.text.Phrase(header, headerFont));
                cell.setBackgroundColor(new java.awt.Color(52, 58, 64));
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Datos
            List<Expediente> expedientes = expedienteRepository.findAll();
            com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);

            for (Expediente exp : expedientes) {
                table.addCell(new com.lowagie.text.Phrase(exp.getIdExpediente().toString(), cellFont));

                // Solicitante
                String solicitante = "N/A";
                if (exp.getPersonaSolicitante() != null && exp.getPersonaSolicitante().getPersona() != null) {
                    solicitante = exp.getPersonaSolicitante().getPersona().getNombres() + " " +
                            exp.getPersonaSolicitante().getPersona().getApellidoPaterno() + " " +
                            exp.getPersonaSolicitante().getPersona().getApellidoMaterno();
                }
                table.addCell(new com.lowagie.text.Phrase(solicitante, cellFont));

                table.addCell(new com.lowagie.text.Phrase(
                        exp.getTipoExpediente() != null ? exp.getTipoExpediente() : "N/A", cellFont));
                table.addCell(new com.lowagie.text.Phrase(
                        exp.getCodigoSeguimiento() != null ? exp.getCodigoSeguimiento() : "N/A", cellFont));
                table.addCell(new com.lowagie.text.Phrase(
                        exp.getEstadoExpediente() != null ? exp.getEstadoExpediente() : "N/A", cellFont));
                table.addCell(new com.lowagie.text.Phrase(
                        exp.getFechaCreacion() != null ? SIMPLE_DATE_FORMAT.format(exp.getFechaCreacion()) : "N/A",
                        cellFont));
                table.addCell(new com.lowagie.text.Phrase(exp.getReseniaSolicitud() != null
                        ? (exp.getReseniaSolicitud().length() > 50 ? exp.getReseniaSolicitud().substring(0, 50) + "..."
                                : exp.getReseniaSolicitud())
                        : "", cellFont));
            }

            document.add(table);

            // Total
            com.lowagie.text.Paragraph total = new com.lowagie.text.Paragraph(
                    "\nTotal de expedientes: " + expedientes.size(),
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD));
            total.setSpacingBefore(10);
            document.add(total);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de expedientes", e);
        }
    }

    /*
     * ===========================
     * GENERAR PDF DE USUARIOS
     * ===========================
     */
    public byte[] generarPdfUsuarios() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
            com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();

            // Título
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                    com.lowagie.text.Font.BOLD);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Reporte de Usuarios", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Fecha
            com.lowagie.text.Font dateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.NORMAL, java.awt.Color.GRAY);
            com.lowagie.text.Paragraph date = new com.lowagie.text.Paragraph(
                    "Generado: " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
            date.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Tabla
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1f, 3f, 2f, 2f, 2f });

            // Encabezados
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.BOLD, java.awt.Color.WHITE);
            String[] headers = { "ID", "Nombre", "Usuario", "Área", "Estado" };

            for (String header : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                        new com.lowagie.text.Phrase(header, headerFont));
                cell.setBackgroundColor(new java.awt.Color(52, 58, 64));
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Datos
            List<Usuario> usuarios = usuarioRepository.findAll();
            com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);

            for (Usuario user : usuarios) {
                table.addCell(new com.lowagie.text.Phrase(user.getIdUsuario().toString(), cellFont));

                // Nombre completo
                String nombreCompleto = "N/A";
                if (user.getPersona() != null) {
                    nombreCompleto = user.getPersona().getNombres() + " " +
                            user.getPersona().getApellidoPaterno() + " " +
                            user.getPersona().getApellidoMaterno();
                }
                table.addCell(new com.lowagie.text.Phrase(nombreCompleto, cellFont));

                table.addCell(new com.lowagie.text.Phrase(
                        user.getNombreUsuario() != null ? user.getNombreUsuario() : "N/A", cellFont));
                table.addCell(new com.lowagie.text.Phrase(
                        user.getIdArea() != null ? user.getIdArea().getNombreArea() : "N/A", cellFont));
                table.addCell(new com.lowagie.text.Phrase(
                        user.getEstadoRegistro() != null && user.getEstadoRegistro().equals("A") ? "Activo"
                                : "Inactivo",
                        cellFont));
            }

            document.add(table);

            // Total
            com.lowagie.text.Paragraph total = new com.lowagie.text.Paragraph("\nTotal de usuarios: " + usuarios.size(),
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD));
            total.setSpacingBefore(10);
            document.add(total);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de usuarios", e);
        }
    }

    /*
     * ===========================
     * EXPORTAR EXPEDIENTES A EXCEL
     * ===========================
     */
    public byte[] exportarExpedientesExcel() {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Expedientes");

            // Estilo para encabezados
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_80_PERCENT.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            // Crear encabezados
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Solicitante", "Tipo", "Código", "Estado", "Fecha Creación", "Reseña" };

            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Obtener datos
            List<Expediente> expedientes = expedienteRepository.findAll();

            // Llenar datos
            int rowNum = 1;
            for (Expediente exp : expedientes) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(exp.getIdExpediente());

                // Solicitante
                String solicitante = "N/A";
                if (exp.getPersonaSolicitante() != null && exp.getPersonaSolicitante().getPersona() != null) {
                    solicitante = exp.getPersonaSolicitante().getPersona().getNombres() + " " +
                            exp.getPersonaSolicitante().getPersona().getApellidoPaterno() + " " +
                            exp.getPersonaSolicitante().getPersona().getApellidoMaterno();
                }
                row.createCell(1).setCellValue(solicitante);

                row.createCell(2).setCellValue(exp.getTipoExpediente() != null ? exp.getTipoExpediente() : "N/A");
                row.createCell(3).setCellValue(exp.getCodigoSeguimiento() != null ? exp.getCodigoSeguimiento() : "N/A");
                row.createCell(4).setCellValue(exp.getEstadoExpediente() != null ? exp.getEstadoExpediente() : "N/A");
                row.createCell(5).setCellValue(
                        exp.getFechaCreacion() != null ? SIMPLE_DATE_FORMAT.format(exp.getFechaCreacion()) : "N/A");
                row.createCell(6).setCellValue(exp.getReseniaSolicitud() != null ? exp.getReseniaSolicitud() : "");
            }

            // Auto-ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir a ByteArray
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al exportar a Excel", e);
        }
    }
}
