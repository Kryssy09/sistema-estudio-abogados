package pe.com.mesadepartes.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageController {

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !auth.getName().equals("anonymousUser");
    }

    @GetMapping("reportes")
    public String reportes(Model model) {
        if (!isAuthenticated()) return "redirect:/login";
        model.addAttribute("pageTitle", "Reportes");
        return "pages/reportes";
    }

    @GetMapping("config")
    public String config(Model model) {
        if (!isAuthenticated()) return "redirect:/login";
        model.addAttribute("pageTitle", "Configuración");
        return "pages/config";
    }
}
