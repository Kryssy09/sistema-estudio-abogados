package pe.com.mesadepartes.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Si no está autenticado, redirigir a login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }
}