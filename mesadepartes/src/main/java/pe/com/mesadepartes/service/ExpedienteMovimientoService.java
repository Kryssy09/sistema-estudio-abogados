package pe.com.mesadepartes.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.entity.ExpedienteMovimiento;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.repository.ExpedienteMovimientoRepository;

@Service
@Transactional
public class ExpedienteMovimientoService {

    @Autowired
    private ExpedienteMovimientoRepository movimientoRepository;

    public void registrarMovimiento(Expediente expediente, Usuario actor, String accion,
            String estadoAnterior, String estadoNuevo,
            Usuario asignadoAnterior, Usuario asignadoNuevo,
            String detalles) {
        ExpedienteMovimiento mov = new ExpedienteMovimiento();
        mov.setExpediente(expediente);
        mov.setUsuarioActor(actor);
        mov.setAccion(accion);
        mov.setEstadoAnterior(estadoAnterior);
        mov.setEstadoNuevo(estadoNuevo);
        mov.setUsuarioAsignadoAnterior(asignadoAnterior);
        mov.setUsuarioAsignadoNuevo(asignadoNuevo);
        mov.setDetalles(detalles);
        mov.setFechaCreacion(new Date());
        mov.setEstadoRegistro("ACT");

        movimientoRepository.save(mov);
    }

    public List<ExpedienteMovimiento> obtenerHistorial(Integer idExpediente) {
        return movimientoRepository.findByExpedienteIdExpedienteOrderByFechaCreacionDesc(idExpediente);
    }
}
