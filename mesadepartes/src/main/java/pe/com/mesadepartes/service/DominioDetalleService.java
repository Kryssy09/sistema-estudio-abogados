package pe.com.mesadepartes.service;

import pe.com.mesadepartes.entity.DominioDetalle;
import pe.com.mesadepartes.repository.DominioDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DominioDetalleService {
    
    @Autowired
    private DominioDetalleRepository dominioDetalleRepository;
    
    public List<DominioDetalle> findByDominioId(int idDominio) {
        return dominioDetalleRepository.findByDominioIdDominio(idDominio);
    }
}