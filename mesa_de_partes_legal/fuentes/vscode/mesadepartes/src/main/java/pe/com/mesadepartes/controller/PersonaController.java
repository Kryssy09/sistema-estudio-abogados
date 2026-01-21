/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.com.mesadepartes.entity.Persona;
import pe.com.mesadepartes.service.PersonaService;
import pe.com.mesadepartes.entity.ResultadoProceso;
import java.util.ArrayList;

/**
 *
 * @author mmoreno
 */
@Controller
public class PersonaController {
    @GetMapping("/persona/listar-todas")
    public String getList(Model model) {
        // PersonaService servicio = new PersonaService();
        // ResultadoProceso rp = servicio.listarTodas();
        // List<Persona> personas = null;

        // if (rp.getCodigoResultado() == 1) {
        // personas = (List<Persona>)rp.getData();
        // } else {
        // personas = new ArrayList<>();
        // }

        // model.addAttribute("listaPersonas", personas);

        // //Retorna a VISTA (archivo HTML)
        return "personas";
    }
}
