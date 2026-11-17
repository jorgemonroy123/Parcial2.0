package com.goldenawards.controller;

import com.goldenawards.config.Estados;
import com.goldenawards.model.CambioEstadoTicket;
import com.goldenawards.model.Ticket;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.TicketRepository;
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
 * Rol SOPORTE TECNICO (API): bandeja de todos los tickets y cambio de estado.
 * REQ-SOP-002: cada cambio de estado guarda el estado, la fecha y quien lo hizo.
 * El controlador web reutiliza los metodos publicos de esta clase.
 */
@RestController
@RequestMapping("/api/soporte")
public class SoporteControladorRest {

    private final UsuarioRepository usuarioRepository;
    private final TicketRepository ticketRepository;

    public SoporteControladorRest(UsuarioRepository usuarioRepository, TicketRepository ticketRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ticketRepository = ticketRepository;
    }

    private ResponseEntity<Object> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }

    private Usuario actual(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName()).orElseThrow();
    }

    private int prioridad(String estado) {
        if (Estados.TICKET_ABIERTO.equals(estado)) return 0;
        if (Estados.TICKET_EN_ATENCION.equals(estado)) return 1;
        return 2;
    }

    // Bandeja: los abiertos primero y, dentro de cada estado, los mas antiguos primero; filtro opcional por estado
    public List<Ticket> bandeja(String estado) {
        List<Ticket> base = estado == null || estado.isBlank()
                ? ticketRepository.findAll() : ticketRepository.findByEstado(estado);
        return base.stream()
                .sorted(Comparator.<Ticket>comparingInt(t -> prioridad(t.getEstado()))
                        .thenComparing(Ticket::getFechaCreacion, Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())))
                .collect(Collectors.toList());
    }

    // Devuelve el mensaje de error o null si el cambio es valido: solo EN_ATENCION o RESUELTO y distinto del actual
    public String validarCambio(Ticket t, String nuevoEstado) {
        if (!Estados.TICKET_EN_ATENCION.equals(nuevoEstado) && !Estados.TICKET_RESUELTO.equals(nuevoEstado)) {
            return "Estado no valido. Usa EN_ATENCION o RESUELTO";
        }
        if (nuevoEstado.equals(t.getEstado())) {
            return "El ticket ya esta en ese estado";
        }
        return null;
    }

    // Aplica el cambio y deja el rastro: estado, fecha y responsable (REQ-SOP-002)
    public Ticket cambiarEstado(Ticket t, String nuevoEstado, Usuario responsable) {
        t.setEstado(nuevoEstado);
        t.getHistorial().add(new CambioEstadoTicket(nuevoEstado, LocalDateTime.now(), responsable.getUsername()));
        return ticketRepository.save(t);
    }

    // Ejemplo: GET /api/soporte/tickets?estado=ABIERTO
    @GetMapping("/tickets")
    public List<Ticket> tickets(@RequestParam(required = false) String estado) {
        return bandeja(estado);
    }

    // Ejemplo: PUT /api/soporte/tickets/{id}/estado?estado=EN_ATENCION
    @PutMapping("/tickets/{id}/estado")
    public ResponseEntity<Object> cambiar(@PathVariable String id, @RequestParam String estado, Principal principal) {
        Ticket t = ticketRepository.findById(id).orElse(null);
        if (t == null) {
            return error(HttpStatus.NOT_FOUND, "Ticket no encontrado");
        }
        String problema = validarCambio(t, estado);
        if (problema != null) {
            return error(HttpStatus.BAD_REQUEST, problema);
        }
        return ResponseEntity.ok(cambiarEstado(t, estado, actual(principal)));
    }
}
