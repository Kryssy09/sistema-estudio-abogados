package pe.com.mesadepartes.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.mesadepartes.entity.Usuario;
import pe.com.mesadepartes.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        System.out.println("--- Intentando cargar usuario: " + nombreUsuario + " ---");
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> {
                    System.out.println("--- Usuario no encontrado: " + nombreUsuario + " ---");
                    return new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario);
                });

        System.out.println("--- Usuario encontrado: " + usuario.getNombreUsuario() + " ---");
        System.out.println("--- Hash de la BD: " + usuario.getClave() + " ---");

        return UserDetailsImpl.build(usuario);
    }

}