package pe.com.mesadepartes.notificador.util;

public class Constantes {
    public static final String APPLICATION_NAME = "Notificador";

    public static final String LOG_INFORMATION = "INFO ";
    public static final String LOG_ERROR = "ERROR";

    public static final String MYSQL_BBDD_IP = "localhost";
    public static final int MYSQL_BBDD_PUERTO = 3306;
    public static final String MYSQL_BBDD_USUARIO = "root";
    public static final String MYSQL_BBDD_NOMBRE = "mesa_de_partes_demo";
    public static final String MYSQL_BBDD_CLAVEUSUARIO = "$mysql";

    //Dominio
    public static final int DOMINIO_TIPO_DOCUMENTO = 1;
    public static final int DOMINIO_SEXO = 2;
    public static final int DOMINIO_PROCESO_NOTIFICACION = 3;
    
    //Dominio-Detalle
    public static final int DOMINIO_DETALLE_NOTIFICACION_CREACION_EXPEDIENTE = 1;
    public static final int DOMINIO_DETALLE_ASUNTO_NOTIFICACION_CREACION_EXPEDIENTE = 2;
    public static final int DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR = 3;
    public static final int DOMINIO_DETALLE_ASUNTO_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR = 4;
    public static final int DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_SOLICITANTE = 5;
    public static final int DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_ESPECIALISTA = 6;

    //SMTP
    public static final String CORREO_CONFIG_CUENTA = "swmiguel4@gmail.com";
    public static final String CORREO_CONFIG_CUENTA_PWD = "xpxj eryj ozpi nbbs";

    //Cuerpo de correo / notificacion - Datos usados por todas las notificaciones
    public static final String CUERPO_CORREO_EXPEDIENTE_CODIGO_SEGUIMIENTO = "$EXPEDIENTE_CODIGO_SEGUIMIENTO";
    public static final String CUERPO_CORREO_CLIENTE_APELLIDOS_Y_NOMBRES = "$CLIENTE_APELLIDOS_Y_NOMBRES";

    //Cuerpo de correo / notificacion por "Creacion de nuevo expediente"
    public static final String CUERPO_CORREO_NUEVO_EXP_EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES = "$EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES";
    public static final String CUERPO_CORREO_NUEVO_EXP_EXPEDIENTE_FECHA_Y_HORA_CREACION = "$EXPEDIENTE_FECHA_Y_HORA_CREACION";

    //Cuerpo de correo / notificacion por "Asignación de expediente"
    public static final String CUERPO_CORREO_ASIG_EXP_EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES = "$EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES";
    public static final String CUERPO_CORREO_ASIG_EXP_AREA_DIRECTOR_APELLIDOS_Y_NOMBRES = "$AREA_DIRECTOR_APELLIDOS_Y_NOMBRES";
    public static final String CUERPO_CORREO_ASIG_EXP_AREA_NOMBRE = "$AREA_NOMBRE";
    public static final String CUERPO_CORREO_ASIG_EXP_EXPEDIENTE_FECHA_Y_HORA_ASIGNACION = "$EXPEDIENTE_FECHA_Y_HORA_ASIGNACION";

    public static final int USUARIO_ADMINISTRADOR_ID = 8;
}
