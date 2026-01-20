package pe.com.mesadepartes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import pe.com.mesadepartes.entity.Persona;
import pe.com.mesadepartes.entity.Rol;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.entity.UsuarioRol;
import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.repository.PersonaRepository;
import pe.com.mesadepartes.repository.SolicitanteRepository;
import pe.com.mesadepartes.repository.UsuarioRepository;
import pe.com.mesadepartes.repository.RolRepository;
import pe.com.mesadepartes.repository.UsuarioRolRepository;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.entity.UsuarioRolId;

import java.time.LocalDateTime;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true", matchIfMissing = false)
public class DataInitializerService implements CommandLineRunner {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Inicializando datos de prueba ===");

        crearRoles();
        crearPersonasYSolicitantes();
        crearUsuariosYRoles();
        crearExpedientes();
        System.out.println("=== Datos inicializados correctamente ===");
    }

    private void crearRoles() {
        if (rolRepository.count() == 0) {
            Rol admin = new Rol();
            admin.setNombreRol("ADMIN");
            admin.setEstadoRegistro("ACT");
            admin.setFechaCreacion(LocalDateTime.now());
            admin.setIdUsuarioCreador(1);
            rolRepository.save(admin);

            Rol coordinador = new Rol();
            coordinador.setNombreRol("COORDINADOR");
            coordinador.setEstadoRegistro("ACT");
            coordinador.setFechaCreacion(LocalDateTime.now());
            coordinador.setIdUsuarioCreador(1);
            rolRepository.save(coordinador);

            Rol abogado = new Rol();
            abogado.setNombreRol("ABOGADO");
            abogado.setEstadoRegistro("ACT");
            abogado.setFechaCreacion(LocalDateTime.now());
            abogado.setIdUsuarioCreador(1);
            rolRepository.save(abogado);

            System.out.println("Roles creados: 3");
        }
    }

    private void crearPersonasYSolicitantes() {
        String[][] solicitantesData = {
            {"Juan Carlos", "Perez", "Gonzales", "12345678", "juan.perez@email.com", "987654321"},
            {"Maria Elena", "Rodriguez", "Mendoza", "23456789", "maria.rodriguez@email.com", "987654322"},
            {"Luis Alberto", "Sanchez", "Fernandez", "34567890", "luis.sanchez@email.com", "987654323"},
            {"Ana Maria", "Garcia", "Lopez", "45678901", "ana.garcia@email.com", "987654324"},
            {"Carlos Andres", "Martinez", "Diaz", "56789012", "carlos.martinez@email.com", "987654325"},
            {"Patricia Lucia", "Ramirez", "Torres", "67890123", "patricia.ramirez@email.com", "987654326"},
            {"Jorge Luis", "Cruz", "Morales", "78901234", "jorge.cruz@email.com", "987654327"},
            {"Sandra Patricia", "Castillo", "Vargas", "89012345", "sandra.castillo@email.com", "987654328"},
            {"Ricardo Jose", "Hernandez", "Silva", "90123456", "ricardo.hernandez@email.com", "987654329"},
            {"Carmen Rosa", "Mendoza", "Perez", "01234567", "carmen.mendoza@email.com", "987654330"}
        };

        for (String[] data : solicitantesData) {
            Persona persona = new Persona();
            persona.setNombres(data[0]);
            persona.setApellidoPaterno(data[1]);
            persona.setApellidoMaterno(data[2]);
            persona.setTipoDocumentoIdentidad(1);
            persona.setNumeroDocumento(data[3]);
            persona.setCorreoElectronico(data[4]);
            persona.setTelefonoPersonal(data[5]);
            persona.setEstadoRegistro("ACT");
            persona.setFechaCreacion(new Date());
            persona.setIdUsuarioCreador(1);
            personaRepository.save(persona);

            Solicitante solicitante = new Solicitante();
            solicitante.setPersona(persona);
            solicitante.setEstadoRegistro("ACT");
            solicitante.setFechaCreacion(LocalDateTime.now());
            solicitante.setIdUsuarioCreador(1);
            solicitanteRepository.save(solicitante);
        }

        System.out.println("Personas y solicitantes creados: 10");
    }

    private void crearUsuariosYRoles() {
        // Usuarios para los primeros 3 solicitantes
        String[] usuarios = {"jpergonz", "mrodrigue", "lsanchez"};

        int i = 0;
        List<Solicitante> solicitantes = solicitanteRepository.findAll();
        if (solicitantes.size() >= usuarios.length) {
            for (i = 0; i < usuarios.length; i++) {
                Solicitante solicitante = solicitantes.get(i);

                Usuario usuario = new Usuario();
                usuario.setPersona(solicitante.getPersona());
                usuario.setNombreUsuario(usuarios[i]);
                usuario.setClave("$2a$10$N.zmdr9z8T4.Za1GU/QxYz8P0p9HhGHpT1XrqFA2x"); // password123
                usuario.setEstadoRegistro("ACT");
                usuario.setFechaCreacion(LocalDateTime.now());
                usuarioRepository.save(usuario);

                Integer rolId = (i == 0) ? 1 : (i == 1) ? 2 : 3; // ADMIN, COORDINADOR, ABOGADO
                Rol rol = rolRepository.findById(rolId).orElse(null);
                if (rol != null) {
                    Integer usuarioId = usuario.getIdUsuario();

                    UsuarioRolId usuarioRolId = new UsuarioRolId();
                    usuarioRolId.setIdUsuario(usuarioId);
                    usuarioRolId.setIdRol(rolId);

                    UsuarioRol usuarioRol = new UsuarioRol();
                    usuarioRol.setId(usuarioRolId);
                    usuarioRol.setUsuario(usuario);
                    usuarioRol.setRol(rol);
                    usuarioRol.setEstadoRegistro("ACT");
                    usuarioRol.setFechaCreacion(LocalDateTime.now());
                    usuarioRol.setIdUsuarioCreador(usuarioId); // Usar el usuario creado
                    usuarioRolRepository.save(usuarioRol);
                }
            }
        }

        System.out.println("Usuarios y roles creados: 3");
    }

    private void crearExpedientes() {
        String[] tiposExpediente = {"CONCILIACION", "ARBITRAJE", "MEDIACION"};
        String[] estados = {"EN_PROCESO", "ATENDIDO", "PENDIENTE"};
        String[] especialidades = {"1", "2", "3"}; // IDs de especialidad

        int solicitanteCount = 1;
        for (Solicitante solicitante : solicitanteRepository.findAll()) {
            if (solicitanteCount > 10) break;

            Expediente expediente = new Expediente();
            expediente.setTipoExpediente(tiposExpediente[(solicitanteCount - 1) % 3]);
            expediente.setMutuoAcuerdo((solicitanteCount % 3) == 0);
            expediente.setEspecialidad(Integer.parseInt(especialidades[(solicitanteCount - 1) % 3]));
            expediente.setCodigoSeguimiento("EXP-" + String.format("%06d", solicitanteCount));
            expediente.setEstadoExpediente(estados[(solicitanteCount - 1) % 3]);
            expediente.setEstadoLegalCaso("EN_TRAMITE");
            expediente.setReseniaSolicitud("Caso iniciado por solicitante " + solicitanteCount);
            expediente.setRutaArchivoFormatoSolicitud("/uploads/formatos/default.pdf");
            expediente.setPersonaSolicitante(solicitante);
            expediente.setEstadoRegistro("ACT");
            expediente.setFechaCreacion(new Date());
            expediente.setIdUsuarioCreador(1);
            expedienteRepository.save(expediente);

            solicitanteCount++;
        }

        System.out.println("Expedientes creados: " + (solicitanteCount - 1));
    }
}