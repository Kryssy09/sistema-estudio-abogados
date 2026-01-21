package pe.com.mesadepartes.notificador.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Area extends EntidadBase {
    private int idArea;
    private String nombre;
    private Persona director;
}
