package pe.com.mesadepartes.controller;

import pe.com.mesadepartes.entity.Dominio;
import pe.com.mesadepartes.entity.DominioDetalle;
import pe.com.mesadepartes.service.DominioService;
import pe.com.mesadepartes.service.DominioDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dominios")
public class DominioController {
    
    @Autowired
    private DominioService dominioService;
    
    @Autowired
    private DominioDetalleService dominioDetalleService;
    
    // Endpoint 1: GET /api/dominios
    @GetMapping
    public List<Dominio> getAllDominios() {
        return dominioService.findAll();
    }
    // Endpoint 2: GET /api/dominios/{id} 
    @GetMapping("/{id}")
    public Dominio getDominioById(@PathVariable int id) {
        return dominioService.findById(id);
}
    
    // Endpoint 3: GET /api/dominios/{id}/detalles
    @GetMapping("/{id}/detalles")
    public List<DominioDetalle> getDetallesByDominio(@PathVariable int id) {
        return dominioDetalleService.findByDominioId(id);
    }
}