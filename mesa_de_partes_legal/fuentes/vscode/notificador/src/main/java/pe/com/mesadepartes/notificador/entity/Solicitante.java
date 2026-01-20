package pe.com.mesadepartes.notificador.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Solicitante {
    private int idSolicitante;
    private Persona persona;

    public Solicitante(Persona persona) {
        this.persona = persona;
    }
}
