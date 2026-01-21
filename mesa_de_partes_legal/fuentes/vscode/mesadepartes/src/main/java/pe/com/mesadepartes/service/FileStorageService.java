package pe.com.mesadepartes.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

@Service
public class FileStorageService {

  @Value("${app.storage.root:${user.home}/mesadepartes/uploads}")
  private String storageRoot;

  private static final Set<String> ALLOWED_EXT = Set.of("pdf", "doc");
  private static final long MAX_BYTES = 10L * 1024 * 1024;

  public String guardarSolicitudExpediente(MultipartFile file) throws Exception {
    if (file == null || file.isEmpty())
      return null;

    if (file.getSize() > MAX_BYTES) {
      throw new IllegalArgumentException("El archivo supera el límite de 10 MB.");
    }

    String original = StringUtils.cleanPath(file.getOriginalFilename());
    String ext = getExtension(original).toLowerCase();

    if (!ALLOWED_EXT.contains(ext)) {
      throw new IllegalArgumentException("Formato no permitido. Solo PDF o DOC.");
    }

    LocalDate today = LocalDate.now();

    Path rootDir = Paths.get(storageRoot).toAbsolutePath().normalize();
    Path dir = rootDir.resolve(Paths.get(
        "expedientes",
        String.valueOf(today.getYear()),
        String.format("%02d", today.getMonthValue()),
        String.format("%02d", today.getDayOfMonth())));

    Files.createDirectories(dir);

    String nombreDestino = today.toEpochDay() + "_solicitud_" + original;
    Path destino = dir.resolve(nombreDestino).normalize();

    if (!destino.startsWith(dir)) {
      throw new SecurityException("Ruta de archivo inválida.");
    }

    try (InputStream in = file.getInputStream()) {
      Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    return String.join("/",
        "expedientes",
        String.valueOf(today.getYear()),
        String.format("%02d", today.getMonthValue()),
        String.format("%02d", today.getDayOfMonth()),
        nombreDestino);

  }

  public String guardarArchivoSesion(Integer idSesion, MultipartFile file) throws Exception {
    if (file == null || file.isEmpty())
      return null;

    LocalDate today = LocalDate.now();
    Path rootDir = Paths.get(storageRoot).toAbsolutePath().normalize();
    String original = StringUtils.cleanPath(file.getOriginalFilename());
    Path dir = rootDir.resolve(Paths.get(
        "sesiones",
        String.valueOf(today.getYear()),
        String.format("%02d", today.getMonthValue()),
        String.format("%02d", today.getDayOfMonth())));

    Files.createDirectories(dir);

    String nombreDestino = today.toEpochDay() + "_sesion_" + idSesion + "_" + original;
    Path destino = dir.resolve(nombreDestino).normalize();

    if (!destino.startsWith(dir)) {
      throw new SecurityException("Ruta de archivo inválida.");
    }

    try (InputStream in = file.getInputStream()) {
      Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    return String.join("/",
        "sesiones",
        String.valueOf(today.getYear()),
        String.format("%02d", today.getMonthValue()),
        String.format("%02d", today.getDayOfMonth()),
        nombreDestino);
  }

  public String guardarFoto(MultipartFile file) throws Exception {
    if (file == null || file.isEmpty())
      return null;

    LocalDate today = LocalDate.now();
    Path rootDir = Paths.get(storageRoot).toAbsolutePath().normalize();
    Path dir = rootDir.resolve(Paths.get(
        "fotos",
        "perfiles"));

    Files.createDirectories(dir);

    String nombreDestino = today.toEpochDay() + "_foto_" + UUID.randomUUID() + "_"
        + StringUtils.cleanPath(file.getOriginalFilename());
    Path destino = dir.resolve(nombreDestino).normalize();

    if (!destino.startsWith(dir)) {
      throw new SecurityException("Ruta de archivo inválida.");
    }

    try (InputStream in = file.getInputStream()) {
      Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    return String.join("/",
        "fotos",
        "perfiles",
        nombreDestino);
  }

  public boolean eliminarArchivo(String rutaRelativa) {
    if (rutaRelativa == null || rutaRelativa.isBlank())
      return false;

    try {
      Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
      Path target = root.resolve(rutaRelativa).normalize();

      if (!target.startsWith(root)) {
        throw new SecurityException("Ruta de archivo inválida: " + rutaRelativa);
      }

      return Files.deleteIfExists(target);
    } catch (Exception ex) {
      System.err.println("No se pudo eliminar archivo físico '" + rutaRelativa + "': " + ex.getMessage());
      return false;
    }
  }

  private static String getExtension(String name) {
    int i = name.lastIndexOf('.');
    return (i == -1) ? "" : name.substring(i + 1);
  }
  // public String guardarFotoPerfil(MultipartFile foto) {
  // if (foto == null || foto.isEmpty())
  // return null;

  // if (foto.getSize() > MAX_BYTES) {
  // throw new FileStorageException("El archivo supera los 2 MB.");
  // }
  // final String contentType = (foto.getContentType() == null) ? "" :
  // foto.getContentType().toLowerCase();
  // if (!ALLOWED.contains(contentType)) {
  // throw new FileStorageException("Formato no permitido. Use JPG, PNG o WEBP.");
  // }

  // try {
  // Files.createDirectories(Path.of(avatarsDirAbs));

  // String ext = switch (contentType) {
  // case "image/jpeg" -> ".jpg";
  // case "image/png" -> ".png";
  // case "image/webp" -> ".webp";
  // default -> "";
  // };

  // String filename = UUID.randomUUID() + ext;
  // Path target = Path.of(avatarsDirAbs, filename);
  // foto.transferTo(target.toFile());

  // // URL pública (sirviéndola con resource handler)
  // return "avatars/" + filename;
  // } catch (Exception ex) {
  // throw new FileStorageException("No se pudo guardar la imagen.");
  // }
  // }

  public static class FileStorageException extends RuntimeException {
    public FileStorageException(String msg) {
      super(msg);
    }
  }
}
