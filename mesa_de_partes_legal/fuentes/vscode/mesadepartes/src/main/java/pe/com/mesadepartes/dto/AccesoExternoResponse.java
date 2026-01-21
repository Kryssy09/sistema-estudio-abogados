package pe.com.mesadepartes.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AccesoExternoResponse implements Serializable {

    private boolean exitoso;
    private String mensaje;
    private String token;
    private LocalDateTime fechaVencimiento;
    private Long idExpediente;
    private String codigoSeguimiento;
    private String estadoExpediente;

    private static final long serialVersionUID = 1L;

    public static AccesoExternoResponse error(String mensaje) {
        AccesoExternoResponse response = new AccesoExternoResponse();
        response.setExitoso(false);
        response.setMensaje(mensaje);
        return response;
    }

    public static AccesoExternoResponse exitoso(String token, LocalDateTime fechaVencimiento,
                                                  Long idExpediente, String codigoSeguimiento, String estadoExpediente) {
        AccesoExternoResponse response = new AccesoExternoResponse();
        response.setExitoso(true);
        response.setMensaje("Acceso concedido correctamente");
        response.setToken(token);
        response.setFechaVencimiento(fechaVencimiento);
        response.setIdExpediente(idExpediente);
        response.setCodigoSeguimiento(codigoSeguimiento);
        response.setEstadoExpediente(estadoExpediente);
        return response;
    }
}