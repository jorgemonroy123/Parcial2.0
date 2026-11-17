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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Rol REPRESENTANTE DEL NOMINADO (API): propone cambios al perfil de SU nominado.
 * REQ-REP-001: la propuesta nace PENDIENTE y NO modifica el perfil publico.
 * REQ-REP-003: solo puede proponer sobre el jugador al que esta vinculado.
 * El Organizador decide en PropuestaPerfilControladorRest. El controlador web reutiliza estos metodos.
 */
@RestController
@RequestMapping("/api/representante")
public class RepresentanteControladorRest {

    public static final int MAX_BIOGRAFIA = 1000;
    public static final int MAX_ESTADISTICA = 9999;

    private final UsuarioRepository usuarioRepository;
    private final JugadorRepository jugadorRepository;
    private final PropuestaPerfilRepository propuestaRepository;

    public RepresentanteControladorRest(UsuarioRepository usuarioRepository, JugadorRepository jugadorRepository,
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

    // El jugador al que esta vinculado el representante (REQ-REP-003); null si no tiene vinculo
    public Jugador jugadorDe(Usuario representante) {
        if (representante.getJugadorId() == null) {
            return null;
        }
        return jugadorRepository.findById(representante.getJugadorId()).orElse(null);
    }

    private String limpiar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private String estadistica(String nombre, Integer valor) {
        if (valor != null && (valor < 0 || valor > MAX_ESTADISTICA)) {
            return nombre + " debe estar entre 0 y " + MAX_ESTADISTICA;
        }
        return null;
    }

    // Devuelve el mensaje de error o null si la propuesta es valida frente al perfil actual
    public String validarPropuesta(Jugador actual, String biografia, Integer partidos, Integer goles,
                                   Integer asistencias, Integer trofeos) {
        String bio = limpiar(biografia);
        if (bio != null && bio.length() > MAX_BIOGRAFIA) {
            return "La biografia no puede superar " + MAX_BIOGRAFIA + " caracteres";
        }
        String[] problemas = {
                estadistica("Los partidos", partidos), estadistica("Los goles", goles),
                estadistica("Las asistencias", asistencias), estadistica("Los trofeos", trofeos)};
        for (String problema : problemas) {
            if (problema != null) {
                return problema;
            }
        }
        boolean sinCambios = Objects.equals(bio, limpiar(actual.getBiografia()))
                && Objects.equals(partidos, actual.getPartidos())
                && Objects.equals(goles, actual.getGoles())
                && Objects.equals(asistencias, actual.getAsistencias())
                && Objects.equals(trofeos, actual.getTrofeos());
        if (sinCambios) {
            return "La propuesta no cambia nada del perfil actual";
        }
        if (propuestaRepository.existsByJugadorIdAndEstado(actual.getId(), Estados.PENDIENTE)) {
            return "Ya hay una propuesta pendiente para este jugador; espera la decision del Organizador";
        }
        return null;
    }

    // Crea la propuesta PENDIENTE. NO toca el perfil del jugador (REQ-REP-001).
    public PropuestaPerfil crearPropuesta(Usuario representante, Jugador jugador, String biografia,
                                          Integer partidos, Integer goles, Integer asistencias, Integer trofeos) {
        PropuestaPerfil p = new PropuestaPerfil();
        p.setJugadorId(jugador.getId());
        p.setJugadorNombre(jugador.getNombre());
        p.setRepresentanteId(representante.getId());
        p.setRepresentanteUsername(representante.getUsername());
        p.setBiografia(limpiar(biografia));
        p.setPartidos(partidos);
        p.setGoles(goles);
        p.setAsistencias(asistencias);
        p.setTrofeos(trofeos);
        p.setEstado(Estados.PENDIENTE);
        p.setFecha(LocalDateTime.now());
        return propuestaRepository.save(p);
    }

    public List<PropuestaPerfil> propuestasDe(Usuario representante) {
        return propuestaRepository.findByRepresentanteId(representante.getId()).stream()
                .sorted(Comparator.comparing(PropuestaPerfil::getFecha,
                        Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    // Perfil publico actual del nominado representado
    @GetMapping("/perfil")
    public ResponseEntity<Object> perfil(Principal principal) {
        Jugador j = jugadorDe(actual(principal));
        if (j == null) {
            return error(HttpStatus.NOT_FOUND, "Tu usuario no esta vinculado a ningun nominado");
        }
        return ResponseEntity.ok(j);
    }

    @GetMapping("/propuestas")
    public List<PropuestaPerfil> misPropuestas(Principal principal) {
        return propuestasDe(actual(principal));
    }

    // Body: {"biografia": "...", "partidos": 40, "goles": 28, "asistencias": 9, "trofeos": 4}
    // El jugador NO se envia: siempre es el vinculado al representante autenticado.
    @PostMapping("/propuestas")
    public ResponseEntity<Object> proponer(@RequestBody PropuestaPerfil datos, Principal principal) {
        Usuario u = actual(principal);
        Jugador j = jugadorDe(u);
        if (j == null) {
            return error(HttpStatus.NOT_FOUND, "Tu usuario no esta vinculado a ningun nominado");
        }
        String problema = validarPropuesta(j, datos.getBiografia(), datos.getPartidos(), datos.getGoles(),
                datos.getAsistencias(), datos.getTrofeos());
        if (problema != null) {
            return error(HttpStatus.BAD_REQUEST, problema);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(crearPropuesta(u, j, datos.getBiografia(),
                datos.getPartidos(), datos.getGoles(), datos.getAsistencias(), datos.getTrofeos()));
    }
}
