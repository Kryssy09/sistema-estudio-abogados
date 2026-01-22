package pe.com.mesadepartes.notificador.service;

import pe.com.mesadepartes.notificador.repository.DominioDetalleRepository;
import pe.com.mesadepartes.notificador.entity.ResultadoProceso;

public class DominioDetalleService {
    private DominioDetalleRepository repositorio;

    public DominioDetalleService() {
        this.repositorio = new DominioDetalleRepository();
    }

    public ResultadoProceso listarDominioDetallePorId(int idDominio, int codigo) {
        return this.repositorio.listarDominioDetallePorId(idDominio, codigo);
    }
}
