package pe.com.mesadepartes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.repository.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public String getDashboardPage(Model model) {
        model.addAttribute("pageTitle", "Dashboard");

        // Obtener datos reales de la base de datos
        long totalExpedientes = expedienteRepository.count();
        long expedientesActivos = expedienteRepository.countByEstadoExpediente("ACTIVO");
        long expedientesCerrados = expedienteRepository.countByEstadoExpediente("CERRADO");
        long usuariosActivos = usuarioRepository.countByEstadoRegistro("A");

        // Calcular tasa de resolución
        double tasaResolucion = totalExpedientes > 0 ? (expedientesCerrados * 100.0 / totalExpedientes) : 0;

        // Crear mapa de estadísticas
        Map<String, String> stats = new HashMap<>();
        stats.put("totalExpedientes", String.valueOf(totalExpedientes));
        stats.put("expedientesActivos", String.valueOf(expedientesActivos));
        stats.put("expedientesCerrados", String.valueOf(expedientesCerrados));
        stats.put("usuariosActivos", String.valueOf(usuariosActivos));
        stats.put("expedientesGrowth", "+0%"); // Placeholder - requiere lógica de comparación temporal
        stats.put("tiempoPromedio", "N/A"); // Placeholder - requiere cálculo de promedio
        stats.put("tasaResolucion", String.format("%.0f%%", tasaResolucion));
        stats.put("usuariosOnline", "0"); // Placeholder - requiere tracking de sesiones

        model.addAttribute("stats", stats);

        return "pages/dashboard";
    }
}
