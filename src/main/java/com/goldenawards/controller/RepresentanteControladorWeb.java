package com.goldenawards.controller;

import com.goldenawards.model.Jugador;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Rol REPRESENTANTE DEL NOMINADO (web): ve el perfil de su nominado y propone cambios.
 * Reutiliza la logica de RepresentanteControladorRest.
 */
@Controller
@RequestMapping("/representante")
public class RepresentanteControladorWeb {

    private final RepresentanteControladorRest representanteRest;
    private final UsuarioRepository usuarioRepository;

    public RepresentanteControladorWeb(RepresentanteControladorRest representanteRest, UsuarioRepository usuarioRepository) {
        this.representanteRest = representanteRest;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario actual(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName()).orElseThrow();
    }

    @GetMapping({"", "/"})
    public String inicio() {
        return "redirect:/representante/perfil";
    }

    @GetMapping("/perfil")
    public String perfil(Principal principal, Model model) {
        Usuario u = actual(principal);
        Jugador j = representanteRest.jugadorDe(u);
        model.addAttribute("jugador", j);
        model.addAttribute("propuestas", representanteRest.propuestasDe(u));
        return "representante/perfil";
    }

    @PostMapping("/propuestas")
    public String proponer(@RequestParam(required = false) String biografia,
                           @RequestParam(required = false) Integer partidos,
                           @RequestParam(required = false) Integer goles,
                           @RequestParam(required = false) Integer asistencias,
                           @RequestParam(required = false) Integer trofeos,
                           Principal principal, RedirectAttributes ra) {
        String destino = "/representante/perfil";
        Usuario u = actual(principal);
        Jugador j = representanteRest.jugadorDe(u);
        if (j == null) {
            ra.addFlashAttribute("mensaje", "Tu usuario no esta vinculado a ningun nominado");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        String problema = representanteRest.validarPropuesta(j, biografia, partidos, goles, asistencias, trofeos);
        if (problema != null) {
            ra.addFlashAttribute("mensaje", problema);
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        representanteRest.crearPropuesta(u, j, biografia, partidos, goles, asistencias, trofeos);
        ra.addFlashAttribute("mensaje", "Propuesta enviada: el perfil publico no cambia hasta que el Organizador la apruebe");
        ra.addFlashAttribute("tipo", "success");
        return "redirect:" + destino;
    }
}
