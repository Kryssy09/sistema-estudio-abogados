package pe.com.mesadepartes.service;

import pe.com.mesadepartes.entity.Dominio;
import pe.com.mesadepartes.repository.DominioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DominioService {
    
    @Autowired
    private DominioRepository dominioRepository;
    
    public List<Dominio> findAll() {
        return dominioRepository.findAll();
    }
    
    public Dominio findById(int id) {
        return dominioRepository.findById(id).orElse(null);
    }
}