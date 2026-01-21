package pe.com.mesadepartes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoDto {
    private String nombre;
    private String nombreSinExtension;
    private String extension;
    private String rutaRelativa;
    private Long tamanio;
}
