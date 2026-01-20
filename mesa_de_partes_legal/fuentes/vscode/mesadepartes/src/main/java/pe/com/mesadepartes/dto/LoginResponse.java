package pe.com.mesadepartes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private Integer idUsuario;
    private String nombreUsuario;
    private String nombreCompleto;
    private String[] roles;
}