package com.goldenawards.controller;

import com.goldenawards.config.Estados;
import com.goldenawards.model.Jugador;
import com.goldenawards.model.PropuestaPerfil;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.JugadorRepository;
import com.goldenawards.repository.PropuestaPerfilRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.stream.Collectors;

/**
 * Bandeja del ORGANIZADOR (web) para las propuestas de perfil del Representante.
 * Reutiliza la logica de PropuestaPerfilControladorRest.
 */
@Controller
@RequestMapping("/organizador/propuestas-perfil")
public class PropuestaPerfilControladorWeb {

    private final PropuestaPerfilControladorRest propuestaRest;
    private final UsuarioRepository usuarioRepository;
    private final JugadorRepository jugadorRepository;
    private final PropuestaPerfilRepository propuestaRepository;

    public PropuestaPerfilControladorWeb(PropuestaPerfilControladorRest propuestaRest, UsuarioRepository usuarioRepository,
                                         JugadorRepository jugadorRepository, PropuestaPerfilRepository propuestaRepository) {
        this.propuestaRest = propuestaRest;
        this.usuarioRepository = usuarioRepository;
        this.jugadorRepository = jugadorRepository;
        this.propuestaRepository = propuestaRepository;
    }

    @GetMapping
    public String bandeja(@RequestParam(required = false) String estado, Model model) {
        String filtro = estado == null || estado.isBlank() ? Estados.PENDIENTE : estado;
        model.addAttribute("propuestas", propuestaRest.bandeja(filtro));
        model.addAttribute("estado", filtro);
        // perfil actual de cada jugador para comparar "actual vs propuesto"
        model.addAttribute("jugadoresPorId", jugadorRepository.findAll().stream()
                .collect(Collectors.toMap(Jugador::getId, j -> j)));
        return "organizador/propuestas-perfil";
    }

    @PostMapping("/{id}/estado")
    public String decidir(@PathVariable String id, @RequestParam String estado,
                          @RequestParam(required = false) String motivo,
                          @RequestParam(required = false) String filtro,
                          Principal principal, RedirectAttributes ra) {
        String destino = "/organizador/propuestas-perfil" + (filtro == null || filtro.isBlank() ? "" : "?estado=" + filtro);
        PropuestaPerfil p = propuestaRepository.findById(id).orElse(null);
        if (p == null) {
            ra.addFlashAttribute("mensaje", "Propuesta no encontrada");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        Usuario organizador = usuarioRepository.findByUsername(principal.getName()).orElseThrow();
        String problema = propuestaRest.decidir(p, estado, motivo, organizador);
        if (problema != null) {
            ra.addFlashAttribute("mensaje", problema);
            ra.addFlashAttribute("tipo", "danger");
        } else {
            ra.addFlashAttribute("mensaje", Estados.APROBADA.equals(estado)
                    ? "Propuesta aprobada: el cambio ya se publicó en el perfil"
                    : "Propuesta rechazada: el perfil queda como estaba");
            ra.addFlashAttribute("tipo", "success");
        }
        return "redirect:" + destino;
    }
}
