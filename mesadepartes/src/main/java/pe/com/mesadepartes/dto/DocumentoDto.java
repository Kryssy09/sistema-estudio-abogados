// pe.com.mesadepartes.dto.DocumentoDto
package pe.com.mesadepartes.dto;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DocumentoDto {
    private Integer id;     // Integer (igual que la entidad)
    private String nombre;
    private String url;
}