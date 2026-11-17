package com.goldenawards.controller;

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
 * "Crear ticket" y "Mis tickets" para TODOS los roles (REQ-SOP-001 y REQ-SOP-003).
 * La validacion y la creacion estan en TicketControladorRest y se reutilizan aqui.
 */
@Controller
@RequestMapping("/tickets")
public class TicketControladorWeb {

    private final TicketControladorRest ticketRest;
    private final UsuarioRepository usuarioRepository;

    public TicketControladorWeb(TicketControladorRest ticketRest, UsuarioRepository usuarioRepository) {
        this.ticketRest = ticketRest;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario actual(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName()).orElseThrow();
    }

    @GetMapping
    public String misTickets(Principal principal, Model model) {
        model.addAttribute("tickets", ticketRest.delUsuario(actual(principal)));
        return "tickets/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo() {
        return "tickets/nuevo";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam String asunto, @RequestParam String descripcion,
                        Principal principal, RedirectAttributes ra) {
        String problema = ticketRest.validar(asunto, descripcion);
        if (problema != null) {
            ra.addFlashAttribute("mensaje", problema);
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:/tickets/nuevo";
        }
        ticketRest.crear(actual(principal), asunto, descripcion);
        ra.addFlashAttribute("mensaje", "Ticket creado: Soporte lo atenderá pronto");
        ra.addFlashAttribute("tipo", "success");
        return "redirect:/tickets";
    }
}
