// pe.com.mesadepartes.repository.DocumentoRepository
package pe.com.mesadepartes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.mesadepartes.entity.DocumentoNormativo;

public interface DocumentoRepository extends JpaRepository<DocumentoNormativo, Integer> {

    List<DocumentoNormativo> findByEstadoRegistroOrderByNombreDocumentoAsc(String estadoRegistro);
}