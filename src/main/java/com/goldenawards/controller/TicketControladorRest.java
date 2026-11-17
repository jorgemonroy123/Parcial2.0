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
 * Tickets de soporte creados por CUALQUIER rol (API). REQ-SOP-001: un ticket nuevo nace ABIERTO a nombre de quien lo crea.
 * REQ-SOP-003: cada usuario solo ve sus propios tickets. El controlador web reutiliza los metodos publicos.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketControladorRest {

    public static final int MAX_ASUNTO = 100;
    public static final int MAX_DESCRIPCION = 1000;

    private final UsuarioRepository usuarioRepository;
    private final TicketRepository ticketRepository;

    public TicketControladorRest(UsuarioRepository usuarioRepository, TicketRepository ticketRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ticketRepository = ticketRepository;
    }

    private ResponseEntity<Object> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }

    private Usuario actual(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName()).orElseThrow();
    }

    // Devuelve el mensaje de error o null si el asunto y la descripcion son validos
    public String validar(String asunto, String descripcion) {
        if (asunto == null || asunto.isBlank()) {
            return "El asunto es obligatorio";
        }
        if (asunto.trim().length() > MAX_ASUNTO) {
            return "El asunto no puede superar " + MAX_ASUNTO + " caracteres";
        }
        if (descripcion == null || descripcion.isBlank()) {
            return "La descripcion es obligatoria";
        }
        if (descripcion.trim().length() > MAX_DESCRIPCION) {
            return "La descripcion no puede superar " + MAX_DESCRIPCION + " caracteres";
        }
        return null;
    }

    // Crea el ticket en estado ABIERTO, a nombre del usuario, con la primera entrada del historial
    public Ticket crear(Usuario autor, String asunto, String descripcion) {
        LocalDateTime ahora = LocalDateTime.now();
        Ticket t = new Ticket();
        t.setAsunto(asunto.trim());
        t.setDescripcion(descripcion.trim());
        t.setEstado(Estados.TICKET_ABIERTO);
        t.setCreadoPorId(autor.getId());
        t.setCreadoPorUsername(autor.getUsername());
        t.setCreadoPorRol(autor.getRol());
        t.setFechaCreacion(ahora);
        t.getHistorial().add(new CambioEstadoTicket(Estados.TICKET_ABIERTO, ahora, autor.getUsername()));
        return ticketRepository.save(t);
    }

    // Solo los tickets del propio usuario, los mas recientes primero (REQ-SOP-003)
    public List<Ticket> delUsuario(Usuario usuario) {
        return ticketRepository.findByCreadoPorId(usuario.getId()).stream()
                .sorted(Comparator.comparing(Ticket::getFechaCreacion,
                        Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    // Mis tickets
    @GetMapping
    public List<Ticket> misTickets(Principal principal) {
        return delUsuario(actual(principal));
    }

    // Body: {"asunto": "...", "descripcion": "..."}
    @PostMapping
    public ResponseEntity<Object> crearTicket(@RequestBody Ticket datos, Principal principal) {
        String problema = validar(datos.getAsunto(), datos.getDescripcion());
        if (problema != null) {
            return error(HttpStatus.BAD_REQUEST, problema);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crear(actual(principal), datos.getAsunto(), datos.getDescripcion()));
    }

    // Un usuario solo puede abrir sus propios tickets; si no es suyo se responde 404 para no revelar que existe
    @GetMapping("/{id}")
    public ResponseEntity<Object> detalle(@PathVariable String id, Principal principal) {
        Usuario u = actual(principal);
        Ticket t = ticketRepository.findById(id).orElse(null);
        if (t == null || !u.getId().equals(t.getCreadoPorId())) {
            return error(HttpStatus.NOT_FOUND, "Ticket no encontrado");
        }
        return ResponseEntity.ok(t);
    }
}
