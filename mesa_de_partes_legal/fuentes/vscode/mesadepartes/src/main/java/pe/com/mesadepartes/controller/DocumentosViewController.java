package pe.com.mesadepartes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.com.mesadepartes.dto.ArchivoDto;
import pe.com.mesadepartes.service.DocumentoService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DocumentosViewController {

    private final DocumentoService documentoService;

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !auth.getName().equals("anonymousUser");
    }

    @GetMapping("/documentos")
    public String documentos(Model model) {
        if (!isAuthenticated()) {
            return "redirect:/login";
        }

        // Cargar archivos de ambas carpetas
        List<ArchivoDto> formatosSolicitud = documentoService.listarArchivosDeCarpeta("formato de solicitud");
        List<ArchivoDto> formatosConciliacion = documentoService.listarArchivosDeCarpeta("formato de conciliacion");

        model.addAttribute("formatosSolicitud", formatosSolicitud);
        model.addAttribute("formatosConciliacion", formatosConciliacion);
        model.addAttribute("pageTitle", "Documentos Informativos");

        return "pages/documentos";
    }
}
