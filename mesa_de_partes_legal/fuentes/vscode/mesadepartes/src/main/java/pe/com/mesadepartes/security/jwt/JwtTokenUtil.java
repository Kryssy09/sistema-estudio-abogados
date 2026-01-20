package pe.com.mesadepartes.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generarToken(Long idTokenAcceso, String dni, Long idExpediente) {
        Date fechaExpiracion = Date.from(LocalDateTime.now().plusHours(24).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(dni)
                .claim("idTokenAcceso", idTokenAcceso)
                .claim("dni", dni)
                .claim("idExpediente", idExpediente)
                .claim("tipo", "ACCESO_EXTERNO")
                .setIssuedAt(new Date())
                .setExpiration(fechaExpiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parsearToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean esTokenValido(String token) {
        try {
            Claims claims = parsearToken(token);
            Date fechaExpiracion = claims.getExpiration();
            return fechaExpiracion != null && !fechaExpiracion.before(new Date())
                    && "ACCESO_EXTERNO".equals(claims.get("tipo"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long obtenerIdTokenAcceso(String token) {
        Claims claims = parsearToken(token);
        return claims.get("idTokenAcceso", Long.class);
    }

    public String obtenerDni(String token) {
        Claims claims = parsearToken(token);
        return claims.getSubject();
    }

    public Long obtenerIdExpediente(String token) {
        Claims claims = parsearToken(token);
        return claims.get("idExpediente", Long.class);
    }

    public String generarClaveAcceso() {
        // Generar una clave alfanumérica de 8 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder clave = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * caracteres.length());
            clave.append(caracteres.charAt(index));
        }

        return clave.toString();
    }

    // Métodos alias para mantener compatibilidad
    public String getDniFromToken(String token) {
        return obtenerDni(token);
    }

    public Long getIdExpedienteFromToken(String token) {
        return obtenerIdExpediente(token);
    }
}