// pe.com.mesadepartes.service.DocumentoService
package pe.com.mesadepartes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.com.mesadepartes.dto.ArchivoDto;
import pe.com.mesadepartes.dto.DocumentoDto;
import pe.com.mesadepartes.entity.DocumentoNormativo;
import pe.com.mesadepartes.repository.DocumentoRepository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentoService {

    private static final Path BASE_PATH = Paths.get("uploads").toAbsolutePath().normalize(); // raíz física
    private final DocumentoRepository documentoRepository;

    /*
     * ===========================
     * LISTAR (por categoría o todo ACT)
     * ===========================
     */

    // Mantengo tu método original (sin categoría)
    public List<DocumentoDto> listarActivos() {
        return documentoRepository
                .findByEstadoRegistroOrderByNombreDocumentoAsc("ACT")
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Nuevo: listar por categoría filtrando por prefijo de ruta (sin cambiar BD)
    public List<DocumentoDto> listarPorCategoria(String categoria) {
        String prefijo = "uploads/" + carpetaPorCategoria(categoria) + "/";
        return documentoRepository
                .findByEstadoRegistroOrderByNombreDocumentoAsc("ACT")
                .stream()
                .filter(d -> {
                    String r = Optional.ofNullable(d.getRutaArchivo()).orElse("").replace("\\", "/");
                    return r.startsWith(prefijo);
                })
                .map(this::toDto)
                .toList();
    }

    private DocumentoDto toDto(DocumentoNormativo d) {
        return DocumentoDto.builder()
                .id(d.getIdDocumentoNormativo())
                .nombre(d.getNombreDocumento())
                .url(publicUrlFromRuta(d.getRutaArchivo()))
                .build();
    }

    private String publicUrlFromRuta(String rutaArchivo) {
        if (rutaArchivo == null)
            return "";
        String norm = rutaArchivo.replace("\\", "/");
        if (norm.startsWith("/"))
            norm = norm.substring(1);
        return "/files/" + norm;
    }

    /*
     * ===========================
     * SUBIR
     * ===========================
     */

    // Versión con categoría (recomendada para tu frontend actual)
    public String guardarArchivo(MultipartFile file, String categoria) throws Exception {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Archivo vacío");

        String carpetaCat = carpetaPorCategoria(categoria); // "solicitudes" | "conciliacion"
        Path carpeta = BASE_PATH.resolve(carpetaCat);
        Files.createDirectories(carpeta);

        String original = Path.of(file.getOriginalFilename()).getFileName().toString();
        String unique = LocalDateTime.now().toString().replace(":", "-") + "_" + original;
        Path destino = carpeta.resolve(unique).normalize();

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        DocumentoNormativo doc = new DocumentoNormativo();
        doc.setNombreDocumento(original);
        // Guardamos ruta relativa respecto a raíz del proyecto:
        // "uploads/<carpeta>/<file>"
        String rutaRelativa = "uploads/" + carpetaCat + "/" + unique;
        doc.setRutaArchivo(rutaRelativa.replace("\\", "/"));
        doc.setEstadoRegistro("ACT");
        doc.setIdUsuarioCreador(1);
        documentoRepository.save(doc);

        // URL pública para frontend
        return "/files/" + rutaRelativa.replace("\\", "/");
    }

    // Versión legacy (sin categoría) → mantiene compatibilidad
    public String guardarArchivo(MultipartFile file) throws Exception {
        return guardarArchivo(file, "SOLICITUD");
    }

    private String carpetaPorCategoria(String categoria) {
        if (categoria == null)
            return "solicitudes";
        String c = categoria.trim().toUpperCase(Locale.ROOT);
        return switch (c) {
            case "SOLICITUD", "SOLICITUDES" -> "solicitudes";
            case "CONCILIACION", "CONCILIACIÓN" -> "conciliacion";
            default -> "solicitudes";
        };
    }

    /*
     * ===========================
     * ELIMINAR
     * ===========================
     */

    public void eliminarDocumento(Integer id) throws Exception {
        DocumentoNormativo doc = documentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + id));

        // Ruta absoluta del archivo desde la ruta guardada ("uploads/.../file")
        Path abs = Paths.get("").toAbsolutePath().resolve(
                Optional.ofNullable(doc.getRutaArchivo()).orElse("")).normalize();

        // Seguridad: debe estar bajo /uploads
        if (!abs.startsWith(BASE_PATH)) {
            throw new SecurityException("Ruta fuera de uploads");
        }

        // 1) borrar físico (si existe)
        try {
            Files.deleteIfExists(abs);
        } catch (Exception ignored) {
        }

        // 2) soft-delete
        doc.setEstadoRegistro("INA");
        documentoRepository.save(doc);

        // Si prefieres hard-delete:
        // documentoRepository.deleteById(id);
    }

    /*
     * ===========================
     * DESCARGAR TODO EN ZIP (por categoría)
     * ===========================
     */

    /**
     * Crea un ZIP en memoria con todos los documentos ACT de una categoría.
     * Devuelve byte[] para que el Controller lo exponga.
     */
    public byte[] zipPorCategoria(String categoria) throws IOException {
        String prefijo = "uploads/" + carpetaPorCategoria(categoria) + "/";

        // Filtramos documentos ACT por prefijo de ruta
        List<DocumentoNormativo> docs = documentoRepository
                .findByEstadoRegistroOrderByNombreDocumentoAsc("ACT")
                .stream()
                .filter(d -> {
                    String r = Optional.ofNullable(d.getRutaArchivo()).orElse("").replace("\\", "/");
                    return r.startsWith(prefijo);
                })
                .toList();

        if (docs.isEmpty())
            return new byte[0];

        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (DocumentoNormativo d : docs) {
                String rutaRel = Optional.ofNullable(d.getRutaArchivo()).orElse("");
                if (rutaRel.isBlank())
                    continue;

                Path abs = Paths.get("").toAbsolutePath().resolve(rutaRel).normalize();
                // Seguridad
                if (!abs.startsWith(BASE_PATH) || !Files.exists(abs) || Files.isDirectory(abs))
                    continue;

                // Nombre dentro del zip (solo el nombre del archivo)
                String fileName = abs.getFileName().toString();

                zos.putNextEntry(new ZipEntry(fileName));
                Files.copy(abs, zos);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    /*
     * ===========================
     * LISTAR ARCHIVOS DE CARPETA (para Formatos)
     * ===========================
     */

    /**
     * Lista todos los archivos de una carpeta específica en el sistema de archivos.
     * Usado para mostrar formatos de solicitud y conciliación.
     */
    public List<ArchivoDto> listarArchivosDeCarpeta(String nombreCarpeta) {
        try {
            // Construir ruta: ${user.home}/mesadepartes/uploads/{nombreCarpeta}
            String userHome = System.getProperty("user.home");
            Path carpetaPath = Paths.get(userHome, "mesadepartes", "uploads", nombreCarpeta);

            // Verificar que la carpeta existe
            if (!Files.exists(carpetaPath) || !Files.isDirectory(carpetaPath)) {
                return Collections.emptyList();
            }

            // Listar archivos (no directorios)
            return Files.list(carpetaPath)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        String nombreCompleto = path.getFileName().toString();
                        String extension = "";
                        String nombreSinExt = nombreCompleto;

                        int lastDot = nombreCompleto.lastIndexOf('.');
                        if (lastDot > 0) {
                            extension = nombreCompleto.substring(lastDot + 1).toLowerCase();
                            nombreSinExt = nombreCompleto.substring(0, lastDot);
                        }

                        long tamanio = 0;
                        try {
                            tamanio = Files.size(path);
                        } catch (IOException ignored) {
                        }

                        ArchivoDto dto = new ArchivoDto();
                        dto.setNombre(nombreCompleto);
                        dto.setNombreSinExtension(nombreSinExt);
                        dto.setExtension(extension);
                        dto.setRutaRelativa(nombreCarpeta + "/" + nombreCompleto);
                        dto.setTamanio(tamanio);

                        return dto;
                    })
                    .sorted(Comparator.comparing(ArchivoDto::getNombre))
                    .toList();

        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}