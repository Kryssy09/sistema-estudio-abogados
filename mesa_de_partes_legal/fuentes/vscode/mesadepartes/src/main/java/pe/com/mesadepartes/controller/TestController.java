package pe.com.mesadepartes.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Contenido público.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return "Contenido de usuario.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Contenido de administrador.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public String moderatorAccess() {
        return "Contenido de moderador.";
    }

    @GetMapping("/auth-info")
    public Map<String, Object> getAuthInfo() {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());
            response.put("principalClass", auth.getPrincipal().getClass().getSimpleName());
        } else {
            response.put("authenticated", false);
            response.put("message", "No autenticado");
        }

        return response;
    }

    @GetMapping("/dashboard-access")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'COORDINADOR', 'ABOGADO')")
    public Map<String, Object> testDashboardAccess() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Acceso al dashboard concedido");
        return response;
    }

    @org.springframework.beans.factory.annotation.Autowired
    private pe.com.mesadepartes.repository.ExpedienteRepository expedienteRepository;

    @GetMapping("/valid-dni")
    public Map<String, Object> getValidDni() {
        Map<String, Object> response = new HashMap<>();
        var expedientes = expedienteRepository.findAll();
        for (var exp : expedientes) {
            if (exp.getPersonaSolicitante() != null && exp.getPersonaSolicitante().getPersona() != null) {
                response.put("dni", exp.getPersonaSolicitante().getPersona().getNumeroDocumento());
                response.put("codigo", exp.getCodigoSeguimiento());
                response.put("idExpediente", exp.getIdExpediente());
                return response;
            }
        }
        response.put("error", "No valid DNI found");
        return response;
    }
}