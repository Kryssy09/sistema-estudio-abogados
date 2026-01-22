package pe.com.mesadepartes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String[] roles;
}