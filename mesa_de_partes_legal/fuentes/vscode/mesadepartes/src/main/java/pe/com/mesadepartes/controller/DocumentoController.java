package pe.com.mesadepartes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.mesadepartes.dto.ArchivoDto;
import pe.com.mesadepartes.dto.DocumentoDto;
import pe.com.mesadepartes.service.DocumentoService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    /*
     * ===========================
     * SUBIR ARCHIVO (con categoría)
     * ===========================
     */
    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> subirArchivo(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "categoria", required = false) String categoria) {
        try {
            String url = documentoService.guardarArchivo(file, categoria);
            return ResponseEntity.ok("Archivo guardado en: " + url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /*
     * ===========================
     * LISTAR (por categoría)
     * ===========================
     */
    @GetMapping("/listar")
    public ResponseEntity<List<DocumentoDto>> listar(
            @RequestParam(value = "categoria", required = false) String categoria) {
        try {
            List<DocumentoDto> docs = (categoria == null || categoria.isBlank())
                    ? documentoService.listarActivos()
                    : documentoService.listarPorCategoria(categoria);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * ===========================
     * ELIMINAR DOCUMENTO
     * ===========================
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            documentoService.eliminarDocumento(id);
            return ResponseEntity.ok("Documento eliminado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /*
     * ===========================
     * DESCARGAR TODOS (ZIP)
     * ===========================
     */
    @GetMapping(value = "/descargar-zip", produces = "application/zip")
    public ResponseEntity<byte[]> descargarZip(
            @RequestParam String categoria) {
        try {
            byte[] zip = documentoService.zipPorCategoria(categoria);
            if (zip.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .header("Content-Disposition", "inline")
                        .body(new byte[0]);
            }

            String fileName = "documentos_" + categoria.toLowerCase(Locale.ROOT) + ".zip";

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zip);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * ===========================
     * DESCARGAR ARCHIVO INDIVIDUAL (desde carpeta)
     * ===========================
     */
    @GetMapping("/descargar")
    public ResponseEntity<Resource> descargarArchivo(
            @RequestParam String carpeta,
            @RequestParam String archivo) {
        try {
            // Construir ruta: ${user.home}/mesadepartes/uploads/{carpeta}/{archivo}
            String userHome = System.getProperty("user.home");
            Path filePath = Paths.get(userHome, "mesadepartes", "uploads", carpeta, archivo);

            // Verificar que el archivo existe
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Crear recurso
            Resource resource = new UrlResource(filePath.toUri());

            // Determinar tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * ===========================
     * BUSCAR ARCHIVOS (predictivo)
     * ===========================
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ArchivoDto>> buscarArchivos(@RequestParam String q) {
        try {
            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            String query = q.toLowerCase().trim();

            // Buscar en ambas carpetas
            List<ArchivoDto> resultados = new ArrayList<>();

            // Buscar en formato de solicitud
            List<ArchivoDto> solicitud = documentoService.listarArchivosDeCarpeta("formato de solicitud");
            solicitud.stream()
                    .filter(archivo -> archivo.getNombreSinExtension().toLowerCase().contains(query) ||
                            archivo.getNombre().toLowerCase().contains(query))
                    .forEach(resultados::add);

            // Buscar en formato de conciliación
            List<ArchivoDto> conciliacion = documentoService.listarArchivosDeCarpeta("formato de conciliacion");
            conciliacion.stream()
                    .filter(archivo -> archivo.getNombreSinExtension().toLowerCase().contains(query) ||
                            archivo.getNombre().toLowerCase().contains(query))
                    .forEach(resultados::add);

            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * ===========================
     * SUBIR ARCHIVO A CARPETA ESPECÍFICA
     * ===========================
     */
    @PostMapping("/subir-formato")
    public ResponseEntity<String> subirFormato(
            @RequestParam("file") MultipartFile file,
            @RequestParam("carpeta") String carpeta) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Archivo vacío");
            }

            // Validar que la carpeta no esté vacía y no contenga caracteres peligrosos
            if (carpeta == null || carpeta.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Carpeta inválida");
            }

            // Sanitizar nombre de carpeta (evitar path traversal)
            String carpetaSanitizada = carpeta.trim().replaceAll("[/\\\\]", "");

            // Construir ruta de destino
            String userHome = System.getProperty("user.home");
            Path carpetaPath = Paths.get(userHome, "mesadepartes", "uploads", carpetaSanitizada);

            // Crear carpeta si no existe
            if (!Files.exists(carpetaPath)) {
                Files.createDirectories(carpetaPath);
            }

            // Guardar archivo con nombre original
            String nombreArchivo = file.getOriginalFilename();
            Path archivoPath = carpetaPath.resolve(nombreArchivo);

            // Verificar si ya existe
            if (Files.exists(archivoPath)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un archivo con ese nombre");
            }

            // Guardar archivo
            file.transferTo(archivoPath.toFile());

            return ResponseEntity.ok("Archivo subido exitosamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir archivo: " + e.getMessage());
        }
    }
}