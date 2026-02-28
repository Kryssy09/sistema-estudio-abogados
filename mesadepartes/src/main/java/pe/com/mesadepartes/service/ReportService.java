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
        public byte[] generarPdfExpedientes(java.util.Date fechaInicio, java.util.Date fechaFin) {
                try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4.rotate());
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

                        document.open();

                        // Título
                        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                                        com.lowagie.text.Font.BOLD);
                        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Reporte de Expedientes",
                                        titleFont);
                        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        title.setSpacingAfter(20);
                        document.add(title);

                        // Fecha de generación
                        com.lowagie.text.Font dateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                                        com.lowagie.text.Font.NORMAL, java.awt.Color.GRAY);

                        String periodoStr = "Periodo: Todos los registros";
                        if (fechaInicio != null && fechaFin != null) {
                                periodoStr = "Periodo: " + new SimpleDateFormat("dd/MM/yyyy").format(fechaInicio)
                                                + " al " + new SimpleDateFormat("dd/MM/yyyy").format(fechaFin);
                        }
                        com.lowagie.text.Paragraph periodo = new com.lowagie.text.Paragraph(periodoStr, dateFont);
                        periodo.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        periodo.setSpacingAfter(10);
                        document.add(periodo);

                        com.lowagie.text.Paragraph date = new com.lowagie.text.Paragraph(
                                        "Generado: " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
                        date.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                        date.setSpacingAfter(10);
                        document.add(date);

                        // Fetch Datos
                        List<Expediente> expedientes;
                        if (fechaInicio != null && fechaFin != null) {
                                expedientes = expedienteRepository.findAllByFechaCreacionBetween(fechaInicio, fechaFin);
                        } else {
                                expedientes = expedienteRepository.findAll();
                        }

                        // Resumen
                        long totalIngresados = expedientes.size();
                        long totalAtendidos = expedientes.stream().filter(e -> "EN_ATE".equals(e.getEstadoExpediente())
                                        || "CERR".equals(e.getEstadoExpediente())).count();
                        long totalPendientes = expedientes.stream()
                                        .filter(e -> "SIN_ASIG".equals(e.getEstadoExpediente())
                                                        || "ASIG".equals(e.getEstadoExpediente()))
                                        .count();

                        com.lowagie.text.Font summaryFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA,
                                        11, com.lowagie.text.Font.BOLD);
                        com.lowagie.text.Paragraph summary = new com.lowagie.text.Paragraph(
                                        String.format("Resumen -> Ingresados: %d  |  Atendidos: %d  |  Pendientes: %d",
                                                        totalIngresados, totalAtendidos, totalPendientes),
                                        summaryFont);
                        summary.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        summary.setSpacingAfter(20);
                        document.add(summary);

                        // Tabla
                        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
                        table.setWidthPercentage(100);
                        table.setWidths(new float[] { 1f, 2f, 2f, 2f, 1.5f, 1.5f, 2f });

                        // Encabezados
                        com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA,
                                        10,
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

                        // Datos ya obtenidos arriba
                        com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);

                        for (Expediente exp : expedientes) {
                                table.addCell(new com.lowagie.text.Phrase(exp.getIdExpediente().toString(), cellFont));

                                // Solicitante
                                String solicitante = "N/A";
                                if (exp.getPersonaSolicitante() != null
                                                && exp.getPersonaSolicitante().getPersona() != null) {
                                        solicitante = exp.getPersonaSolicitante().getPersona().getNombres() + " " +
                                                        exp.getPersonaSolicitante().getPersona().getApellidoPaterno()
                                                        + " " +
                                                        exp.getPersonaSolicitante().getPersona().getApellidoMaterno();
                                }
                                table.addCell(new com.lowagie.text.Phrase(solicitante, cellFont));

                                table.addCell(new com.lowagie.text.Phrase(
                                                exp.getTipoExpediente() != null ? exp.getTipoExpediente() : "N/A",
                                                cellFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                exp.getCodigoSeguimiento() != null ? exp.getCodigoSeguimiento() : "N/A",
                                                cellFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                exp.getEstadoExpediente() != null ? exp.getEstadoExpediente() : "N/A",
                                                cellFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                exp.getFechaCreacion() != null
                                                                ? SIMPLE_DATE_FORMAT.format(exp.getFechaCreacion())
                                                                : "N/A",
                                                cellFont));
                                table.addCell(new com.lowagie.text.Phrase(exp.getReseniaSolicitud() != null
                                                ? (exp.getReseniaSolicitud().length() > 50
                                                                ? exp.getReseniaSolicitud().substring(0, 50) + "..."
                                                                : exp.getReseniaSolicitud())
                                                : "", cellFont));
                        }

                        document.add(table);

                        // Total
                        com.lowagie.text.Paragraph total = new com.lowagie.text.Paragraph(
                                        "\nTotal de expedientes: " + expedientes.size(),
                                        new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12,
                                                        com.lowagie.text.Font.BOLD));
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
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4);
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

                        document.open();

                        // Título
                        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                                        com.lowagie.text.Font.BOLD);
                        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Reporte de Usuarios",
                                        titleFont);
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
                        com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA,
                                        10,
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
                                                user.getNombreUsuario() != null ? user.getNombreUsuario() : "N/A",
                                                cellFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                user.getIdArea() != null ? user.getIdArea().getNombreArea() : "N/A",
                                                cellFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                user.getEstadoRegistro() != null && user.getEstadoRegistro().equals("A")
                                                                ? "Activo"
                                                                : "Inactivo",
                                                cellFont));
                        }

                        document.add(table);

                        // Total
                        com.lowagie.text.Paragraph total = new com.lowagie.text.Paragraph(
                                        "\nTotal de usuarios: " + usuarios.size(),
                                        new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12,
                                                        com.lowagie.text.Font.BOLD));
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
        public byte[] exportarExpedientesExcel(java.util.Date fechaInicio, java.util.Date fechaFin) {
                try {
                        org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Expedientes");

                        // Estilo para encabezados
                        org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
                        headerStyle.setFont(headerFont);
                        headerStyle.setFillForegroundColor(
                                        org.apache.poi.ss.usermodel.IndexedColors.GREY_80_PERCENT.getIndex());
                        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

                        // Obtener datos
                        List<Expediente> expedientes;
                        if (fechaInicio != null && fechaFin != null) {
                                expedientes = expedienteRepository.findAllByFechaCreacionBetween(fechaInicio, fechaFin);
                        } else {
                                expedientes = expedienteRepository.findAll();
                        }

                        // Resumen
                        long totalIngresados = expedientes.size();
                        long totalAtendidos = expedientes.stream().filter(e -> "EN_ATE".equals(e.getEstadoExpediente())
                                        || "CERR".equals(e.getEstadoExpediente())).count();
                        long totalPendientes = expedientes.stream()
                                        .filter(e -> "SIN_ASIG".equals(e.getEstadoExpediente())
                                                        || "ASIG".equals(e.getEstadoExpediente()))
                                        .count();

                        org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
                        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                        String periodoStr = (fechaInicio != null && fechaFin != null)
                                        ? "Periodo: " + new SimpleDateFormat("dd/MM/yyyy").format(fechaInicio) + " al "
                                                        + new SimpleDateFormat("dd/MM/yyyy").format(fechaFin)
                                        : "Periodo: Todos los registros";
                        titleCell.setCellValue("Reporte de Expedientes - " + periodoStr);

                        org.apache.poi.ss.usermodel.Row summaryRow = sheet.createRow(1);
                        summaryRow.createCell(0)
                                        .setCellValue(String.format("Ingresados: %d | Atendidos: %d | Pendientes: %d",
                                                        totalIngresados, totalAtendidos, totalPendientes));

                        // Crear encabezados
                        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(3);
                        String[] headers = { "ID", "Solicitante", "Tipo", "Código", "Estado", "Fecha Creación",
                                        "Reseña" };

                        for (int i = 0; i < headers.length; i++) {
                                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                                cell.setCellStyle(headerStyle);
                        }

                        // Llenar datos
                        int rowNum = 4;
                        for (Expediente exp : expedientes) {
                                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);

                                row.createCell(0).setCellValue(exp.getIdExpediente());

                                // Solicitante
                                String solicitante = "N/A";
                                if (exp.getPersonaSolicitante() != null
                                                && exp.getPersonaSolicitante().getPersona() != null) {
                                        solicitante = exp.getPersonaSolicitante().getPersona().getNombres() + " " +
                                                        exp.getPersonaSolicitante().getPersona().getApellidoPaterno()
                                                        + " " +
                                                        exp.getPersonaSolicitante().getPersona().getApellidoMaterno();
                                }
                                row.createCell(1).setCellValue(solicitante);

                                row.createCell(2).setCellValue(
                                                exp.getTipoExpediente() != null ? exp.getTipoExpediente() : "N/A");
                                row.createCell(3)
                                                .setCellValue(exp.getCodigoSeguimiento() != null
                                                                ? exp.getCodigoSeguimiento()
                                                                : "N/A");
                                row.createCell(4).setCellValue(
                                                exp.getEstadoExpediente() != null ? exp.getEstadoExpediente() : "N/A");
                                row.createCell(5).setCellValue(
                                                exp.getFechaCreacion() != null
                                                                ? SIMPLE_DATE_FORMAT.format(exp.getFechaCreacion())
                                                                : "N/A");
                                row.createCell(6).setCellValue(
                                                exp.getReseniaSolicitud() != null ? exp.getReseniaSolicitud() : "");
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

        /*
         * ===========================
         * GENERAR PDF CARGO DE RECEPCION
         * ===========================
         */
        public byte[] generarPdfCargo(Expediente expediente) {
                try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4, 50, 50, 60, 60);
                        com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document,
                                        baos);
                        document.open();

                        // ── Colores ──
                        java.awt.Color colorPrimario = new java.awt.Color(31, 78, 121); // azul oscuro
                        java.awt.Color colorSecundario = new java.awt.Color(214, 234, 248); // azul claro
                        java.awt.Color colorTextoClaro = java.awt.Color.WHITE;
                        java.awt.Color colorGris = new java.awt.Color(236, 240, 241);

                        // ── Fuentes ──
                        com.lowagie.text.Font fuenteTituloGrande = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA, 22, com.lowagie.text.Font.BOLD,
                                        colorTextoClaro);
                        com.lowagie.text.Font fuenteSubtitulo = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.NORMAL,
                                        colorTextoClaro);
                        com.lowagie.text.Font fuenteEtiqueta = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD,
                                        colorPrimario);
                        com.lowagie.text.Font fuenteValor = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL,
                                        java.awt.Color.BLACK);
                        com.lowagie.text.Font fuentePie = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.ITALIC,
                                        java.awt.Color.GRAY);
                        com.lowagie.text.Font fuenteCodigo = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.COURIER, 16, com.lowagie.text.Font.BOLD,
                                        colorPrimario);

                        // ── Cabecera con fondo de color ──
                        com.lowagie.text.pdf.PdfPTable headerTable = new com.lowagie.text.pdf.PdfPTable(1);
                        headerTable.setWidthPercentage(100);
                        com.lowagie.text.pdf.PdfPCell headerCell = new com.lowagie.text.pdf.PdfPCell();
                        headerCell.setBackgroundColor(colorPrimario);
                        headerCell.setPadding(18);
                        headerCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);

                        com.lowagie.text.Paragraph institutionName = new com.lowagie.text.Paragraph(
                                        "ESTUDIO DE ABOGADOS", fuenteTituloGrande);
                        institutionName.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        com.lowagie.text.Paragraph cargoTitle = new com.lowagie.text.Paragraph(
                                        "CARGO DE RECEPCIÓN DE EXPEDIENTE", fuenteSubtitulo);
                        cargoTitle.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);

                        headerCell.addElement(institutionName);
                        headerCell.addElement(cargoTitle);
                        headerTable.addCell(headerCell);
                        document.add(headerTable);

                        // ── Código de expediente destacado ──
                        com.lowagie.text.pdf.PdfPTable codigoTable = new com.lowagie.text.pdf.PdfPTable(1);
                        codigoTable.setWidthPercentage(100);
                        com.lowagie.text.pdf.PdfPCell codigoCell = new com.lowagie.text.pdf.PdfPCell();
                        codigoCell.setBackgroundColor(colorSecundario);
                        codigoCell.setPadding(12);
                        codigoCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                        com.lowagie.text.Paragraph codigoParagraph = new com.lowagie.text.Paragraph(
                                        "N° EXPEDIENTE: " + expediente.getCodigoSeguimiento(), fuenteCodigo);
                        codigoParagraph.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        codigoCell.addElement(codigoParagraph);
                        codigoTable.addCell(codigoCell);
                        document.add(codigoTable);

                        document.add(new com.lowagie.text.Paragraph("\n"));

                        // ── Tabla de datos ──
                        com.lowagie.text.pdf.PdfPTable dataTable = new com.lowagie.text.pdf.PdfPTable(2);
                        dataTable.setWidthPercentage(100);
                        dataTable.setWidths(new float[] { 2f, 4f });
                        dataTable.setSpacingBefore(5);

                        // Solicitante
                        String solicitante = "-";
                        if (expediente.getPersonaSolicitante() != null
                                        && expediente.getPersonaSolicitante().getPersona() != null) {
                                solicitante = expediente.getPersonaSolicitante().getPersona().getNombres() + " "
                                                + expediente.getPersonaSolicitante().getPersona().getApellidoPaterno()
                                                + " "
                                                + expediente.getPersonaSolicitante().getPersona().getApellidoMaterno();
                        }

                        // Tipo trámite
                        String tipoTramite = expediente.getTipoExpediente() != null ? expediente.getTipoExpediente()
                                        : "-";
                        if ("CON".equals(tipoTramite))
                                tipoTramite = "Conciliación";
                        else if ("PL".equals(tipoTramite))
                                tipoTramite = "Patrocinio Legal";

                        String fechaHora = expediente.getFechaCreacion() != null
                                        ? SIMPLE_DATE_FORMAT.format(expediente.getFechaCreacion())
                                        : "-";

                        String[][] filas = {
                                        { "Solicitante", solicitante },
                                        { "Tipo de Trámite", tipoTramite },
                                        { "Fecha y Hora de Recepción", fechaHora },
                                        { "Código de Seguimiento", expediente.getCodigoSeguimiento() },
                                        { "Estado Inicial", "Sin Asignar" },
                        };

                        boolean sombreado = false;
                        for (String[] fila : filas) {
                                java.awt.Color bgColor = sombreado ? colorGris : java.awt.Color.WHITE;

                                com.lowagie.text.pdf.PdfPCell etiquetaCell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(fila[0], fuenteEtiqueta));
                                etiquetaCell.setBackgroundColor(bgColor);
                                etiquetaCell.setPadding(8);
                                etiquetaCell.setBorderColor(new java.awt.Color(189, 195, 199));

                                com.lowagie.text.pdf.PdfPCell valorCell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(fila[1], fuenteValor));
                                valorCell.setBackgroundColor(bgColor);
                                valorCell.setPadding(8);
                                valorCell.setBorderColor(new java.awt.Color(189, 195, 199));

                                dataTable.addCell(etiquetaCell);
                                dataTable.addCell(valorCell);
                                sombreado = !sombreado;
                        }
                        document.add(dataTable);

                        document.add(new com.lowagie.text.Paragraph("\n\n"));

                        // ── Sección de firmas ──
                        com.lowagie.text.pdf.PdfPTable firmasTable = new com.lowagie.text.pdf.PdfPTable(2);
                        firmasTable.setWidthPercentage(80);
                        firmasTable.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        firmasTable.setWidths(new float[] { 1f, 1f });
                        firmasTable.setSpacingBefore(20);

                        com.lowagie.text.Font fuenteFirma = new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.NORMAL,
                                        java.awt.Color.GRAY);

                        for (String label : new String[] { "Firma del Solicitante", "Sello / Firma Responsable" }) {
                                com.lowagie.text.pdf.PdfPCell firmaCell = new com.lowagie.text.pdf.PdfPCell();
                                firmaCell.setFixedHeight(60);
                                firmaCell.setBorder(com.lowagie.text.Rectangle.BOTTOM);
                                firmaCell.setBorderWidth(1);
                                firmaCell.setBorderColor(java.awt.Color.GRAY);
                                firmaCell.setPaddingTop(45);
                                firmaCell.setPaddingLeft(10);
                                firmaCell.setPaddingRight(10);
                                firmaCell.setBorderWidthLeft(0);
                                firmaCell.setBorderWidthRight(0);
                                firmaCell.setBorderWidthTop(0);
                                com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(label, fuenteFirma);
                                p.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                                firmaCell.addElement(p);
                                firmasTable.addCell(firmaCell);
                        }
                        document.add(firmasTable);

                        // ── Pie de página ──
                        document.add(new com.lowagie.text.Paragraph("\n"));
                        com.lowagie.text.Paragraph footer = new com.lowagie.text.Paragraph(
                                        "Documento generado el " + LocalDateTime.now().format(DATE_FORMATTER)
                                                        + "  |  Sistema de Mesa de Partes - IINCADE 4.0\n"
                                                        + "Este comprobante es válido como constancia formal de la recepción del trámite.",
                                        fuentePie);
                        footer.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        document.add(footer);

                        document.close();
                        return baos.toByteArray();

                } catch (Exception e) {
                        throw new RuntimeException("Error al generar PDF de cargo de recepción", e);
                }
        }
}
