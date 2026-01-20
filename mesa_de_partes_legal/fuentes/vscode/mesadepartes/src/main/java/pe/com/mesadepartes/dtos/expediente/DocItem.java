package pe.com.mesadepartes.dtos.expediente;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocItem {
  private String titulo;
  private MultipartFile file;
}
