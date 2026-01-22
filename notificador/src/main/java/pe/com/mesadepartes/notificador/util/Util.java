package pe.com.mesadepartes.notificador.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static String obtenerFechaYHoraActual() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    public static void escribirLog(String aplicacion, String tipo, String cadena) {
        System.out.println(obtenerFechaYHoraActual() + " - " + aplicacion + " [" + tipo + "] " + cadena);
    }
}
