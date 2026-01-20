package pe.com.mesadepartes.repository;

import pe.com.mesadepartes.entity.DominioDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DominioDetalleRepository extends JpaRepository<DominioDetalle, Integer> {
    
        List<DominioDetalle> findByDominioIdDominio(int idDominio);
}