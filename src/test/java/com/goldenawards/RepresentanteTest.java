package com.goldenawards;

import com.goldenawards.config.Estados;
import com.goldenawards.controller.PropuestaPerfilControladorRest;
import com.goldenawards.controller.RepresentanteControladorRest;
import com.goldenawards.model.Jugador;
import com.goldenawards.model.PropuestaPerfil;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.JugadorRepository;
import com.goldenawards.repository.PropuestaPerfilRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * T027 - Pruebas de aceptacion del Representante del nominado (Sprint 2).
 *  REQ-REP-001: la propuesta nace PENDIENTE y NO cambia el perfil publico.
 *  AC-REP-002.1 / REQ-REP-002: si el Organizador aprueba, el perfil muestra el cambio; si rechaza, queda igual.
 *  REQ-REP-003: el representante solo propone sobre el jugador al que esta vinculado.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RepresentanteTest {

    @Mock private UsuarioRepository usuarios;
    @Mock private JugadorRepository jugadores;
    @Mock private PropuestaPerfilRepository propuestas;

    private RepresentanteControladorRest representanteRest;
    private PropuestaPerfilControladorRest bandejaRest;
    private Usuario representante;
    private Usuario organizador;
    private Jugador jugador;

    @BeforeEach
    void preparar() {
        representante = usuario("R1", "representante", "REPRESENTANTE");
        representante.setJugadorId("J1");
        organizador = usuario("O1", "organizador", "ORGANIZADOR");

        jugador = new Jugador();
        jugador.setId("J1");
        jugador.setNombre("Mateo Rivera");
        jugador.setBiografia("Biografia original");
        jugador.setPartidos(30);
        jugador.setGoles(20);
        jugador.setAsistencias(5);
        jugador.setTrofeos(2);

        when(usuarios.findByUsername("representante")).thenReturn(Optional.of(representante));
        when(usuarios.findByUsername("organizador")).thenReturn(Optional.of(organizador));
        when(jugadores.findById("J1")).thenReturn(Optional.of(jugador));
        when(jugadores.save(any(Jugador.class))).thenAnswer(inv -> inv.getArgument(0));
        when(propuestas.save(any(PropuestaPerfil.class))).thenAnswer(inv -> inv.getArgument(0));

        representanteRest = new RepresentanteControladorRest(usuarios, jugadores, propuestas);
        bandejaRest = new PropuestaPerfilControladorRest(usuarios, jugadores, propuestas);
    }

    private Usuario usuario(String id, String username, String rol) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setUsername(username);
        u.setRol(rol);
        return u;
    }

    private PropuestaPerfil propuestaDeEjemplo() {
        return representanteRest.crearPropuesta(representante, jugador, "Biografia nueva", 30, 25, 5, 2);
    }

    // ---------- REQ-REP-001 ----------

    @Test
    void laPropuestaNacePendienteYNoCambiaElPerfil() {
        PropuestaPerfil p = propuestaDeEjemplo();

        assertEquals(Estados.PENDIENTE, p.getEstado());
        assertEquals("J1", p.getJugadorId());
        assertEquals("representante", p.getRepresentanteUsername());
        verify(jugadores, never()).save(any(Jugador.class));
        assertEquals("Biografia original", jugador.getBiografia(), "el perfil publico sigue igual");
        assertEquals(20, jugador.getGoles());
    }

    @Test
    void validaLaPropuesta() {
        assertNotNull(representanteRest.validarPropuesta(jugador, "Biografia original", 30, 20, 5, 2),
                "sin cambios");
        assertNotNull(representanteRest.validarPropuesta(jugador, "Nueva", 30, -1, 5, 2), "estadistica negativa");
        assertNotNull(representanteRest.validarPropuesta(jugador, "x".repeat(RepresentanteControladorRest.MAX_BIOGRAFIA + 1), 30, 20, 5, 2));
        assertNull(representanteRest.validarPropuesta(jugador, "Nueva", 30, 20, 5, 2));
    }

    @Test
    void soloUnaPropuestaPendientePorJugador() {
        when(propuestas.existsByJugadorIdAndEstado("J1", Estados.PENDIENTE)).thenReturn(true);

        assertNotNull(representanteRest.validarPropuesta(jugador, "Nueva", 30, 20, 5, 2));
    }

    // ---------- AC-REP-002.1 / REQ-REP-002 ----------

    @Test
    void alAprobarElPerfilMuestraElCambio() {
        PropuestaPerfil p = propuestaDeEjemplo();

        String problema = bandejaRest.decidir(p, Estados.APROBADA, null, organizador);

        assertNull(problema);
        assertEquals(Estados.APROBADA, p.getEstado());
        assertEquals("organizador", p.getDecididoPor());
        assertNotNull(p.getDecididoEn());
        ArgumentCaptor<Jugador> captor = ArgumentCaptor.forClass(Jugador.class);
        verify(jugadores).save(captor.capture());
        assertEquals("Biografia nueva", captor.getValue().getBiografia());
        assertEquals(25, captor.getValue().getGoles());
    }

    @Test
    void alRechazarElPerfilQuedaIgual() {
        PropuestaPerfil p = propuestaDeEjemplo();

        String problema = bandejaRest.decidir(p, Estados.RECHAZADA, "Datos sin respaldo", organizador);

        assertNull(problema);
        assertEquals(Estados.RECHAZADA, p.getEstado());
        assertEquals("Datos sin respaldo", p.getMotivo());
        verify(jugadores, never()).save(any(Jugador.class));
        assertEquals("Biografia original", jugador.getBiografia());
        assertEquals(20, jugador.getGoles());
    }

    @Test
    void unaPropuestaYaDecididaNoSeDecideDeNuevo() {
        PropuestaPerfil p = propuestaDeEjemplo();
        bandejaRest.decidir(p, Estados.RECHAZADA, null, organizador);

        assertNotNull(bandejaRest.decidir(p, Estados.APROBADA, null, organizador));
        verify(jugadores, never()).save(any(Jugador.class));
    }

    // ---------- REQ-REP-003 ----------

    @Test
    void elRepresentanteSoloActuaSobreSuNominado() {
        assertEquals("J1", representanteRest.jugadorDe(representante).getId());

        Usuario sinVinculo = usuario("R2", "sinvinculo", "REPRESENTANTE");
        assertNull(representanteRest.jugadorDe(sinVinculo));
    }

    @Test
    void aunqueElClienteMandeOtroJugadorLaPropuestaVaAlSuyo() {
        PropuestaPerfil pedido = new PropuestaPerfil();
        pedido.setJugadorId("OTRO-JUGADOR");   // se ignora
        pedido.setBiografia("Biografia nueva");
        pedido.setPartidos(30);
        pedido.setGoles(25);
        pedido.setAsistencias(5);
        pedido.setTrofeos(2);
        Principal quien = () -> "representante";

        ResponseEntity<Object> respuesta = representanteRest.proponer(pedido, quien);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        ArgumentCaptor<PropuestaPerfil> captor = ArgumentCaptor.forClass(PropuestaPerfil.class);
        verify(propuestas).save(captor.capture());
        assertEquals("J1", captor.getValue().getJugadorId());
    }
}
