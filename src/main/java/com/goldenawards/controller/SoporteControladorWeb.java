package com.goldenawards.controller;

import com.goldenawards.model.Ticket;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.TicketRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Rol SOPORTE TECNICO (web): bandeja de tickets y cambio de estado.
 * Reutiliza la logica de SoporteControladorRest.
 */
@Controller
@RequestMapping("/soporte")
public class SoporteControladorWeb {

    private final SoporteControladorRest soporteRest;
    private final UsuarioRepository usuarioRepository;
    private final TicketRepository ticketRepository;

    public SoporteControladorWeb(SoporteControladorRest soporteRest, UsuarioRepository usuarioRepository,
                                 TicketRepository ticketRepository) {
        this.soporteRest = soporteRest;
        this.usuarioRepository = usuarioRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping({"", "/"})
    public String inicio() {
        return "redirect:/soporte/tickets";
    }

    @GetMapping("/tickets")
    public String bandeja(@RequestParam(required = false) String estado, Model model) {
        model.addAttribute("tickets", soporteRest.bandeja(estado));
        model.addAttribute("estado", estado == null ? "" : estado);
        return "soporte/tickets";
    }

    @PostMapping("/tickets/{id}/estado")
    public String cambiarEstado(@PathVariable String id, @RequestParam String estado,
                                @RequestParam(required = false) String filtro,
                                Principal principal, RedirectAttributes ra) {
        String destino = "/soporte/tickets" + (filtro == null || filtro.isBlank() ? "" : "?estado=" + filtro);
        Ticket t = ticketRepository.findById(id).orElse(null);
        if (t == null) {
            ra.addFlashAttribute("mensaje", "Ticket no encontrado");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        String problema = soporteRest.validarCambio(t, estado);
        if (problema != null) {
            ra.addFlashAttribute("mensaje", problema);
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        Usuario soporte = usuarioRepository.findByUsername(principal.getName()).orElseThrow();
        soporteRest.cambiarEstado(t, estado, soporte);
        ra.addFlashAttribute("mensaje", "Ticket actualizado");
        ra.addFlashAttribute("tipo", "success");
        return "redirect:" + destino;
    }
}
