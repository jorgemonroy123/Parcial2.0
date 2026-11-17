package com.goldenawards.controller;

import com.goldenawards.config.Estados;
import com.goldenawards.model.Feedback;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.FeedbackRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Rol MODERADOR (API): cola de comentarios del publico; aprobar u ocultar.
 * REQ-MOD-001 (los comentarios nuevos nacen PENDIENTES y ocultos, ver Estados),
 * REQ-MOD-002 (el moderador aprueba u oculta) y REQ-MOD-003 (el publico solo ve los aprobados).
 * El controlador web reutiliza los metodos publicos de esta clase.
 */
@RestController
@RequestMapping("/api/moderador")
public class ModeradorControladorRest {

    public static final int MAX_MOTIVO = 200;

    private final UsuarioRepository usuarioRepository;
    private final FeedbackRepository feedbackRepository;

    public ModeradorControladorRest(UsuarioRepository usuarioRepository, FeedbackRepository feedbackRepository) {
        this.usuarioRepository = usuarioRepository;
        this.feedbackRepository = feedbackRepository;
    }

    private ResponseEntity<Object> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }

    private Usuario actual(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName()).orElseThrow();
    }

    // Comentarios con texto en un estado (por defecto PENDIENTE), los mas recientes primero
    public List<Feedback> comentarios(String estado) {
        String filtro = estado == null || estado.isBlank() ? Estados.PENDIENTE : estado;
        return feedbackRepository.findByEstadoComentario(filtro).stream()
                .filter(Estados::tieneComentario)
                .sorted(Comparator.comparing(Feedback::getFecha,
                        Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    // Devuelve el mensaje de error o null si la decision es valida
    public String validarDecision(Feedback f, String estado) {
        if (!Estados.esDecision(estado)) {
            return "Estado no valido. Usa APROBADA o RECHAZADA";
        }
        if (!Estados.tieneComentario(f)) {
            return "Este registro no tiene comentario que moderar";
        }
        if (estado.equals(f.getEstadoComentario())) {
            return "El comentario ya esta en ese estado";
        }
        return null;
    }

    // Guarda la decision con quien la tomo y cuando (C-05)
    public Feedback decidir(Feedback f, String estado, Usuario moderador) {
        f.setEstadoComentario(estado);
        f.setModeradoPor(moderador.getUsername());
        f.setModeradoEn(LocalDateTime.now());
        return feedbackRepository.save(f);
    }

    // Ejemplo: GET /api/moderador/comentarios?estado=PENDIENTE
    @GetMapping("/comentarios")
    public List<Feedback> listar(@RequestParam(required = false) String estado) {
        return comentarios(estado);
    }

    // Ejemplo: PUT /api/moderador/comentarios/{id}/estado?estado=APROBADA  (o RECHAZADA para ocultarlo)
    @PutMapping("/comentarios/{id}/estado")
    public ResponseEntity<Object> moderar(@PathVariable String id, @RequestParam String estado, Principal principal) {
        Feedback f = feedbackRepository.findById(id).orElse(null);
        if (f == null) {
            return error(HttpStatus.NOT_FOUND, "Comentario no encontrado");
        }
        String problema = validarDecision(f, estado);
        if (problema != null) {
            return error(HttpStatus.BAD_REQUEST, problema);
        }
        return ResponseEntity.ok(decidir(f, estado, actual(principal)));
    }
}
