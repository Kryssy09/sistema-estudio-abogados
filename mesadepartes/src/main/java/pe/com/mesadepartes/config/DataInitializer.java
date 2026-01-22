package pe.com.mesadepartes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.com.mesadepartes.entity.*;
import pe.com.mesadepartes.repository.PersonaRepository;
import pe.com.mesadepartes.repository.RolRepository;
import pe.com.mesadepartes.repository.UsuarioRepository;
import pe.com.mesadepartes.repository.UsuarioRolRepository;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true", matchIfMissing = false)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar si no hay usuarios
        if (usuarioRepository.count() == 0) {
            initializeTestData();
        }
    }

    private void initializeTestData() {
        System.out.println("Inicializando datos de usuarios del proyecto...");

        // Crear roles
        Rol rolAdmin = new Rol();
        rolAdmin.setNombreRol("ROLE_ADMIN");
        rolAdmin.setEstadoRegistro("ACTV");
        rolAdmin.setIdUsuarioCreador(1);
        rolRepository.save(rolAdmin);

        Rol rolCoordinador = new Rol();
        rolCoordinador.setNombreRol("ROLE_COORDINADOR");
        rolCoordinador.setEstadoRegistro("ACTV");
        rolCoordinador.setIdUsuarioCreador(1);
        rolRepository.save(rolCoordinador);

        Rol rolAbogado = new Rol();
        rolAbogado.setNombreRol("ROLE_ABOGADO");
        rolAbogado.setEstadoRegistro("ACTV");
        rolAbogado.setIdUsuarioCreador(1);
        rolRepository.save(rolAbogado);

        // Crear personas y usuarios
        // 1. Miguel Moreno - Admin
        Persona personaMiguel = new Persona();
        personaMiguel.setNombres("Miguel");
        personaMiguel.setApellidoPaterno("Moreno");
        personaMiguel.setApellidoMaterno("");
        personaMiguel.setTipoDocumentoIdentidad(1);
        personaMiguel.setNumeroDocumento("12345678");
        personaMiguel.setEstadoRegistro("ACTV");
        personaMiguel.setIdUsuarioCreador(1);
        personaMiguel.setFechaCreacion(new java.util.Date());
        personaRepository.save(personaMiguel);

        Usuario usuarioMiguel = new Usuario();
        usuarioMiguel.setPersona(personaMiguel);
        usuarioMiguel.setNombreUsuario("mmoreno");
        usuarioMiguel.setClave(passwordEncoder.encode("mmorenoadmin123"));
        usuarioMiguel.setEstadoRegistro("ACTV");
        usuarioMiguel.setFechaCreacion(LocalDateTime.now());
        usuarioRepository.save(usuarioMiguel);

        System.out.println("DEBUG: Contraseña codificada para mmoreno: " + usuarioMiguel.getClave());

        // Asignar rol ADMIN a Miguel
        UsuarioRolId usuarioRolIdMiguel = new UsuarioRolId();
        usuarioRolIdMiguel.setIdUsuario(usuarioMiguel.getIdUsuario());
        usuarioRolIdMiguel.setIdRol(rolAdmin.getId());

        UsuarioRol usuarioRolMiguel = new UsuarioRol();
        usuarioRolMiguel.setId(usuarioRolIdMiguel);
        usuarioRolMiguel.setUsuario(usuarioMiguel);
        usuarioRolMiguel.setRol(rolAdmin);
        usuarioRolMiguel.setEstadoRegistro("ACTV");
        usuarioRolMiguel.setIdUsuarioCreador(usuarioMiguel.getIdUsuario());
        usuarioRolMiguel.setFechaCreacion(LocalDateTime.now());
        usuarioRolRepository.save(usuarioRolMiguel);

        // 2. Roger Guillen - Coordinador
        Persona personaRoger = new Persona();
        personaRoger.setNombres("Roger");
        personaRoger.setApellidoPaterno("Guillen");
        personaRoger.setApellidoMaterno("");
        personaRoger.setTipoDocumentoIdentidad(1);
        personaRoger.setNumeroDocumento("87654321");
        personaRoger.setEstadoRegistro("ACTV");
        personaRoger.setIdUsuarioCreador(1);
        personaRoger.setFechaCreacion(new java.util.Date());
        personaRepository.save(personaRoger);

        Usuario usuarioRoger = new Usuario();
        usuarioRoger.setPersona(personaRoger);
        usuarioRoger.setNombreUsuario("rguillen");
        usuarioRoger.setClave(passwordEncoder.encode("rguillenadmin123"));
        usuarioRoger.setEstadoRegistro("ACTV");
        usuarioRoger.setFechaCreacion(LocalDateTime.now());
        usuarioRepository.save(usuarioRoger);

        // Asignar rol COORDINADOR a Roger
        UsuarioRolId usuarioRolIdRoger = new UsuarioRolId();
        usuarioRolIdRoger.setIdUsuario(usuarioRoger.getIdUsuario());
        usuarioRolIdRoger.setIdRol(rolCoordinador.getId());

        UsuarioRol usuarioRolRoger = new UsuarioRol();
        usuarioRolRoger.setId(usuarioRolIdRoger);
        usuarioRolRoger.setUsuario(usuarioRoger);
        usuarioRolRoger.setRol(rolCoordinador);
        usuarioRolRoger.setEstadoRegistro("ACTV");
        usuarioRolRoger.setIdUsuarioCreador(usuarioRoger.getIdUsuario());
        usuarioRolRoger.setFechaCreacion(LocalDateTime.now());
        usuarioRolRepository.save(usuarioRolRoger);

        // 3. Carlos Cardenas - Abogado
        Persona personaCarlos = new Persona();
        personaCarlos.setNombres("Carlos");
        personaCarlos.setApellidoPaterno("Cardenas");
        personaCarlos.setApellidoMaterno("");
        personaCarlos.setTipoDocumentoIdentidad(1);
        personaCarlos.setNumeroDocumento("11223344");
        personaCarlos.setEstadoRegistro("ACTV");
        personaCarlos.setIdUsuarioCreador(1);
        personaCarlos.setFechaCreacion(new java.util.Date());
        personaRepository.save(personaCarlos);

        Usuario usuarioCarlos = new Usuario();
        usuarioCarlos.setPersona(personaCarlos);
        usuarioCarlos.setNombreUsuario("ccardenas");
        usuarioCarlos.setClave(passwordEncoder.encode("ccardenasadmin123"));
        usuarioCarlos.setEstadoRegistro("ACTV");
        usuarioCarlos.setFechaCreacion(LocalDateTime.now());
        usuarioRepository.save(usuarioCarlos);

        // Asignar rol ABOGADO a Carlos
        UsuarioRolId usuarioRolIdCarlos = new UsuarioRolId();
        usuarioRolIdCarlos.setIdUsuario(usuarioCarlos.getIdUsuario());
        usuarioRolIdCarlos.setIdRol(rolAbogado.getId());

        UsuarioRol usuarioRolCarlos = new UsuarioRol();
        usuarioRolCarlos.setId(usuarioRolIdCarlos);
        usuarioRolCarlos.setUsuario(usuarioCarlos);
        usuarioRolCarlos.setRol(rolAbogado);
        usuarioRolCarlos.setEstadoRegistro("ACTV");
        usuarioRolCarlos.setIdUsuarioCreador(usuarioCarlos.getIdUsuario());
        usuarioRolCarlos.setFechaCreacion(LocalDateTime.now());
        usuarioRolRepository.save(usuarioRolCarlos);

        System.out.println("Usuarios del proyecto inicializados:");
        System.out.println("1. Usuario: mmoreno | Contraseña: mmorenoadmin123 | Rol: ADMIN");
        System.out.println("2. Usuario: rguillen | Contraseña: rguillenadmin123 | Rol: COORDINADOR");
        System.out.println("3. Usuario: ccardenas | Contraseña: ccardenasadmin123 | Rol: ABOGADO");
    }
}