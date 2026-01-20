package pe.com.mesadepartes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsultaExternaController {

    @GetMapping("/consulta-externa")
    public String consultaExterna() {
        return "forward:/consulta-externa.html";
    }
}