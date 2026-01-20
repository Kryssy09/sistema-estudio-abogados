package pe.com.mesadepartes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.mesadepartes.dto.ChartPoint;
import pe.com.mesadepartes.repository.ExpedienteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ExpedienteRepository expedienteRepository;

    public List<ChartPoint> expedientesPorTipo() {
        return expedienteRepository.contarPorTipoSinFiltros().stream()
                .map(r -> new ChartPoint((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }
}