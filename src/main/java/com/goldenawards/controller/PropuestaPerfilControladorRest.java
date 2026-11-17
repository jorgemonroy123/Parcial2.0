package com.goldenawards.controller;

import com.goldenawards.config.Estados;
import com.goldenawards.model.Jugador;
import com.goldenawards.model.PropuestaPerfil;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.JugadorRepository;
import com.goldenawards.repository.PropuestaPerfilRepository;
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
 * Bandeja del ORGANIZADOR (API) para las propuestas de perfil del Representante.
 * REQ-REP-002: si aprueba, el cambio se publica en el perfil del jugador; si rechaza, el perfil queda igual.
 * Cada decision guarda quien la tomo y cuando (C-05). El controlador web reutiliza los metodos publicos.
 */
@RestController
@RequestMapping("/api/organizador/propuestas-perfil")
public class PropuestaPerfilControladorRest {

    public static final int MAX_MOTIVO = 200;

    private final UsuarioRepository usuarioRepository;
    private final JugadorRepository jugadorRepository;
    private final PropuestaPerfilRepository propuestaRepository;

    public PropuestaPerfilControladorRest(UsuarioRepository usuarioRepository, JugadorRepository jugadorRepository,
                                          PropuestaPerfilRepository propuestaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.jugadorRepository = jugadorRepository;
        this.propuestaRepository = propuestaRepository;
    }

    private ResponseEntity<Object> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }

    private Usuario actual(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName()).orElseThrow();
    }

    // Propuestas en un estado (por defecto PENDIENTE), las mas antiguas primero
    public List<PropuestaPerfil> bandeja(String estado) {
        String filtro = estado == null || estado.isBlank() ? Estados.PENDIENTE : estado;
        return propuestaRepository.findByEstado(filtro).stream()
                .sorted(Comparator.comparing(PropuestaPerfil::getFecha,
                        Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Aprueba o rechaza una propuesta PENDIENTE. Devuelve el mensaje de error o null si todo salio bien.
     * APROBADA: copia los valores propuestos al jugador (publica el cambio). RECHAZADA: no toca al jugador.
     */
    public String decidir(PropuestaPerfil p, String estado, String motivo, Usuario organizador) {
        if (!Estados.esDecision(estado)) {
            return "Estado no valido. Usa APROBADA o RECHAZADA";
        }
        if (!Estados.PENDIENTE.equals(p.getEstado())) {
            return "La propuesta ya fue decidida";
        }
        String razon = motivo == null || motivo.isBlank() ? null : motivo.trim();
        if (razon != null && razon.length() > MAX_MOTIVO) {
            return "El motivo no puede superar " + MAX_MOTIVO + " caracteres";
        }
        if (Estados.APROBADA.equals(estado)) {
            Jugador j = jugadorRepository.findById(p.getJugadorId()).orElse(null);
            if (j == null) {
                return "El jugador de la propuesta ya no existe";
            }
            j.setBiografia(p.getBiografia());
            j.setPartidos(p.getPartidos());
            j.setGoles(p.getGoles());
            j.setAsistencias(p.getAsistencias());
            j.setTrofeos(p.getTrofeos());
            jugadorRepository.save(j);
        }
        p.setEstado(estado);
        p.setDecididoPor(organizador.getUsername());
        p.setDecididoEn(LocalDateTime.now());
        p.setMotivo(razon);
        propuestaRepository.save(p);
        return null;
    }

    // Ejemplo: GET /api/organizador/propuestas-perfil?estado=PENDIENTE
    @GetMapping
    public List<PropuestaPerfil> listar(@RequestParam(required = false) String estado) {
        return bandeja(estado);
    }

    // Ejemplo: PUT /api/organizador/propuestas-perfil/{id}/estado?estado=APROBADA&motivo=opcional
    @PutMapping("/{id}/estado")
    public ResponseEntity<Object> decidirPropuesta(@PathVariable String id, @RequestParam String estado,
                                                   @RequestParam(required = false) String motivo, Principal principal) {
        PropuestaPerfil p = propuestaRepository.findById(id).orElse(null);
        if (p == null) {
            return error(HttpStatus.NOT_FOUND, "Propuesta no encontrada");
        }
        String problema = decidir(p, estado, motivo, actual(principal));
        if (problema != null) {
            return error(HttpStatus.BAD_REQUEST, problema);
        }
        return ResponseEntity.ok(p);
    }
}
