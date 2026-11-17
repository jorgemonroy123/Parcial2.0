package com.goldenawards;

import com.goldenawards.config.Estados;
import com.goldenawards.controller.SoporteControladorRest;
import com.goldenawards.controller.TicketControladorRest;
import com.goldenawards.model.CambioEstadoTicket;
import com.goldenawards.model.Ticket;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.TicketRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * T027 - Pruebas de aceptacion del Soporte tecnico (Sprint 2).
 *  REQ-SOP-001: un ticket nuevo queda ABIERTO a nombre de quien lo crea (cualquier rol).
 *  AC-SOP-002.1 / REQ-SOP-002: al pasarlo a EN_ATENCION se guarda estado, fecha y quien lo hizo.
 *  REQ-SOP-003: cada usuario solo ve sus propios tickets.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SoporteTest {

    @Mock private UsuarioRepository usuarios;
    @Mock private TicketRepository tickets;

    private TicketControladorRest ticketRest;
    private SoporteControladorRest soporteRest;
    private Usuario votante;
    private Usuario soporte;

    @BeforeEach
    void preparar() {
        votante = usuario("U1", "votante", "VOTANTE");
        soporte = usuario("U9", "soporte", "SOPORTE");
        when(usuarios.findByUsername("votante")).thenReturn(Optional.of(votante));
        when(usuarios.findByUsername("soporte")).thenReturn(Optional.of(soporte));
        when(tickets.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        ticketRest = new TicketControladorRest(usuarios, tickets);
        soporteRest = new SoporteControladorRest(usuarios, tickets);
    }

    private Usuario usuario(String id, String username, String rol) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setUsername(username);
        u.setRol(rol);
        return u;
    }

    @Test
    void unTicketNuevoQuedaAbiertoANombreDeQuienLoCrea() {
        Ticket t = ticketRest.crear(votante, "No puedo votar", "Me sale un error al votar");

        assertEquals(Estados.TICKET_ABIERTO, t.getEstado());
        assertEquals("U1", t.getCreadoPorId());
        assertEquals("votante", t.getCreadoPorUsername());
        assertEquals("VOTANTE", t.getCreadoPorRol());
        assertNotNull(t.getFechaCreacion());
        assertEquals(1, t.getHistorial().size());
        assertEquals("votante", t.getHistorial().get(0).getResponsable());
    }

    @Test
    void validaAsuntoYDescripcion() {
        assertNotNull(ticketRest.validar("", "texto"));
        assertNotNull(ticketRest.validar("asunto", "  "));
        assertNotNull(ticketRest.validar("x".repeat(TicketControladorRest.MAX_ASUNTO + 1), "texto"));
        assertNotNull(ticketRest.validar("asunto", "x".repeat(TicketControladorRest.MAX_DESCRIPCION + 1)));
        assertNull(ticketRest.validar("asunto", "descripcion"));
    }

    @Test
    void alPasarloAEnAtencionSeGuardaEstadoFechaYResponsable() {
        Ticket t = ticketRest.crear(votante, "Asunto", "Descripcion");

        assertNull(soporteRest.validarCambio(t, Estados.TICKET_EN_ATENCION));
        soporteRest.cambiarEstado(t, Estados.TICKET_EN_ATENCION, soporte);

        assertEquals(Estados.TICKET_EN_ATENCION, t.getEstado());
        assertEquals(2, t.getHistorial().size());
        CambioEstadoTicket ultimo = t.getHistorial().get(1);
        assertEquals(Estados.TICKET_EN_ATENCION, ultimo.getEstado());
        assertNotNull(ultimo.getFecha());
        assertEquals("soporte", ultimo.getResponsable());
    }

    @Test
    void rechazaCambiosInvalidos() {
        Ticket t = ticketRest.crear(votante, "Asunto", "Descripcion");

        assertNotNull(soporteRest.validarCambio(t, Estados.TICKET_ABIERTO), "no se puede volver a ABIERTO");
        assertNotNull(soporteRest.validarCambio(t, "OTRO"));
        soporteRest.cambiarEstado(t, Estados.TICKET_RESUELTO, soporte);
        assertNotNull(soporteRest.validarCambio(t, Estados.TICKET_RESUELTO), "ya esta en ese estado");
    }

    @Test
    void cadaUsuarioSoloVeSusPropiosTickets() {
        Ticket suyo = ticketRest.crear(votante, "Mio", "x");
        when(tickets.findByCreadoPorId("U1")).thenReturn(List.of(suyo));

        List<Ticket> mios = ticketRest.delUsuario(votante);

        assertEquals(1, mios.size());
        assertEquals("U1", mios.get(0).getCreadoPorId());
    }

    @Test
    void unTicketAjenoRespondeComoSiNoExistiera() {
        Ticket deOtro = ticketRest.crear(votante, "Mio", "x");
        deOtro.setId("T1");
        when(tickets.findById("T1")).thenReturn(Optional.of(deOtro));
        Principal otroUsuario = () -> "soporte";   // otro usuario distinto del creador

        assertEquals(HttpStatus.NOT_FOUND, ticketRest.detalle("T1", otroUsuario).getStatusCode());
        Principal elCreador = () -> "votante";
        assertEquals(HttpStatus.OK, ticketRest.detalle("T1", elCreador).getStatusCode());
    }
}
