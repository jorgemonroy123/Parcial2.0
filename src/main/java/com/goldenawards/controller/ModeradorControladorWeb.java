package com.goldenawards.controller;

import com.goldenawards.config.Estados;
import com.goldenawards.model.Feedback;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.FeedbackRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Rol MODERADOR (web): cola de comentarios del publico para aprobar u ocultar.
 * Reutiliza la logica de ModeradorControladorRest.
 */
@Controller
@RequestMapping("/moderador")
public class ModeradorControladorWeb {

    private final ModeradorControladorRest moderadorRest;
    private final UsuarioRepository usuarioRepository;
    private final FeedbackRepository feedbackRepository;

    public ModeradorControladorWeb(ModeradorControladorRest moderadorRest, UsuarioRepository usuarioRepository,
                                   FeedbackRepository feedbackRepository) {
        this.moderadorRest = moderadorRest;
        this.usuarioRepository = usuarioRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping({"", "/"})
    public String inicio() {
        return "redirect:/moderador/comentarios";
    }

    @GetMapping("/comentarios")
    public String cola(@RequestParam(required = false) String estado, Model model) {
        String filtro = estado == null || estado.isBlank() ? Estados.PENDIENTE : estado;
        model.addAttribute("comentarios", moderadorRest.comentarios(filtro));
        model.addAttribute("estado", filtro);
        return "moderador/comentarios";
    }

    @PostMapping("/comentarios/{id}/estado")
    public String moderar(@PathVariable String id, @RequestParam String estado,
                          @RequestParam(required = false) String filtro,
                          Principal principal, RedirectAttributes ra) {
        String destino = "/moderador/comentarios" + (filtro == null || filtro.isBlank() ? "" : "?estado=" + filtro);
        Feedback f = feedbackRepository.findById(id).orElse(null);
        if (f == null) {
            ra.addFlashAttribute("mensaje", "Comentario no encontrado");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        String problema = moderadorRest.validarDecision(f, estado);
        if (problema != null) {
            ra.addFlashAttribute("mensaje", problema);
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:" + destino;
        }
        Usuario moderador = usuarioRepository.findByUsername(principal.getName()).orElseThrow();
        moderadorRest.decidir(f, estado, moderador);
        ra.addFlashAttribute("mensaje", Estados.APROBADA.equals(estado)
                ? "Comentario aprobado: ya es visible para el público"
                : "Comentario oculto: el público ya no lo ve");
        ra.addFlashAttribute("tipo", "success");
        return "redirect:" + destino;
    }
}
