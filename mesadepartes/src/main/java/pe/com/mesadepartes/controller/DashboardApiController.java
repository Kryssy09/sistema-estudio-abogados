package pe.com.mesadepartes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.mesadepartes.dto.ChartPoint;
import pe.com.mesadepartes.service.DashboardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DashboardApiController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard/expedientes-por-tipo")
    public List<ChartPoint> expedientesPorTipo() {
        return dashboardService.expedientesPorTipo();
    }
}