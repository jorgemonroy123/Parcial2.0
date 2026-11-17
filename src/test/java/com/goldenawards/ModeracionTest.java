package com.goldenawards;

import com.goldenawards.config.Estados;
import com.goldenawards.controller.ModeradorControladorRest;
import com.goldenawards.model.Feedback;
import com.goldenawards.model.Usuario;
import com.goldenawards.repository.FeedbackRepository;
import com.goldenawards.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * T027 - Pruebas de aceptacion del Moderador (Sprint 2).
 *  REQ-MOD-001: un comentario nuevo nace PENDIENTE y oculto.
 *  AC-MOD-002.1 / REQ-MOD-002: al aprobarlo se muestra; al ocultarlo, no (y queda quien y cuando).
 *  REQ-MOD-003: el publico solo ve los comentarios aprobados (o las calificaciones sin comentario).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModeracionTest {

    @Mock private UsuarioRepository usuarios;
    @Mock private FeedbackRepository feedbacks;

    private ModeradorControladorRest moderadorRest;
    private Usuario moderador;

    @BeforeEach
    void preparar() {
        moderador = new Usuario();
        moderador.setId("M1");
        moderador.setUsername("moderador");
        moderador.setRol("MODERADOR");
        when(feedbacks.save(any(Feedback.class))).thenAnswer(inv -> inv.getArgument(0));
        moderadorRest = new ModeradorControladorRest(usuarios, feedbacks);
    }

    private Feedback comentario(String texto) {
        Feedback f = new Feedback();
        f.setComentario(texto);
        f.setFecha(LocalDateTime.now());
        f.setEstadoComentario(Estados.estadoInicialComentario(texto));
        return f;
    }

    @Test
    void unComentarioNuevoNaceVisibleSoloParaElModerador() {
        Feedback f = comentario("Muy buena organizacion");

        assertEquals(Estados.PENDIENTE, f.getEstadoComentario());
        assertFalse(Estados.comentarioPublico(f), "pendiente = oculto para el publico");
    }

    @Test
    void unaCalificacionSinTextoNoNecesitaModeracion() {
        Feedback f = comentario("   ");

        assertNull(f.getEstadoComentario());
        assertTrue(Estados.comentarioPublico(f));
    }

    @Test
    void alAprobarloSeMuestraYQuedaQuienYCuando() {
        Feedback f = comentario("Excelente");

        assertNull(moderadorRest.validarDecision(f, Estados.APROBADA));
        moderadorRest.decidir(f, Estados.APROBADA, moderador);

        assertEquals(Estados.APROBADA, f.getEstadoComentario());
        assertEquals("moderador", f.getModeradoPor());
        assertNotNull(f.getModeradoEn());
        assertTrue(Estados.comentarioPublico(f));
    }

    @Test
    void alOcultarloNoSeMuestra() {
        Feedback f = comentario("Texto ofensivo");

        moderadorRest.decidir(f, Estados.RECHAZADA, moderador);

        assertEquals(Estados.RECHAZADA, f.getEstadoComentario());
        assertFalse(Estados.comentarioPublico(f));
    }

    @Test
    void sePuedeCambiarUnaDecisionPeroNoRepetirla() {
        Feedback f = comentario("Texto");
        moderadorRest.decidir(f, Estados.APROBADA, moderador);

        assertNotNull(moderadorRest.validarDecision(f, Estados.APROBADA), "ya esta aprobado");
        assertNull(moderadorRest.validarDecision(f, Estados.RECHAZADA), "puede ocultarse despues");
        assertNotNull(moderadorRest.validarDecision(f, "PENDIENTE"), "no se vuelve a PENDIENTE");
    }

    @Test
    void noSeModeraUnRegistroSinComentario() {
        assertNotNull(moderadorRest.validarDecision(comentario(""), Estados.APROBADA));
    }

    @Test
    void laColaMuestraSoloLosComentariosConTextoDelEstadoPedido() {
        Feedback pendiente = comentario("Pendiente");
        when(feedbacks.findByEstadoComentario(Estados.PENDIENTE)).thenReturn(List.of(pendiente));

        assertEquals(1, moderadorRest.comentarios(null).size(), "por defecto muestra los PENDIENTES");
        assertEquals(0, moderadorRest.comentarios(Estados.APROBADA).size());
    }
}
