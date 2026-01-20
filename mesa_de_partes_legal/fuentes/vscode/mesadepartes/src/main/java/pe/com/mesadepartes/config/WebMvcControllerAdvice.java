package pe.com.mesadepartes.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class WebMvcControllerAdvice {

    @ModelAttribute
    public void addUserAttributes(org.springframework.ui.Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String username = auth.getName();
            String displayName = username;

            // Mapeo de usernames a nombres completos
            if (username.equals("admin")) {
                displayName = "Miguel Moreno";
            } else if (username.equals("rguillen")) {
                displayName = "Roger Guillen";
            } else if (username.equals("ccardenas")) {
                displayName = "Carlos Cardenas";
            }

            // Obtener iniciales para el avatar
            String[] nameParts = displayName.split(" ");
            String initials = "";
            for (String part : nameParts) {
                if (!part.isEmpty()) {
                    initials += part.charAt(0);
                }
            }
            initials = initials.length() > 2 ? initials.substring(0, 2) : initials;

            // Determinar el rol del usuario
            String userRole = "USER";
            String roleDisplay = "Usuario";
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                userRole = "ADMIN";
                roleDisplay = "Administrador";
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COORDINADOR"))) {
                userRole = "COORDINADOR";
                roleDisplay = "Coordinador";
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ABOGADO"))) {
                userRole = "ABOGADO";
                roleDisplay = "Abogado";
            }

            model.addAttribute("userDisplayName", displayName);
            model.addAttribute("userInitials", initials.toUpperCase());
            model.addAttribute("userName", username);
            model.addAttribute("userRole", userRole);
            model.addAttribute("roleDisplay", roleDisplay);
        }
    }
}