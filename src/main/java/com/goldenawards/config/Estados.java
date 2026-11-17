package com.goldenawards.config;

import com.goldenawards.model.Feedback;

/**
 * Estados reutilizables de los flujos entre roles (ADR-004, C-05).
 * Mismo ciclo PENDIENTE -> APROBADA | RECHAZADA que ya usan las nominaciones; ahora tambien lo usan los
 * comentarios (Moderador) y las propuestas de perfil (Representante -> Organizador). Cada decision guarda
 * quien la tomo y cuando. Clase utilitaria: no es un service.
 */
public final class Estados {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADA = "APROBADA";
    public static final String RECHAZADA = "RECHAZADA";

    // Estados de los tickets de soporte
    public static final String TICKET_ABIERTO = "ABIERTO";
    public static final String TICKET_EN_ATENCION = "EN_ATENCION";
    public static final String TICKET_RESUELTO = "RESUELTO";

    private Estados() {}

    public static boolean esDecision(String estado) {
        return APROBADA.equals(estado) || RECHAZADA.equals(estado);
    }

    public static boolean tieneComentario(Feedback f) {
        return f.getComentario() != null && !f.getComentario().isBlank();
    }

    /** Un comentario es publico si no tiene texto (solo estrellas) o si el Moderador lo aprobo (REQ-MOD-003). */
    public static boolean comentarioPublico(Feedback f) {
        return !tieneComentario(f) || APROBADA.equals(f.getEstadoComentario());
    }

    /** Estado inicial de un comentario nuevo: PENDIENTE y oculto (REQ-MOD-001). Sin texto no hay nada que moderar. */
    public static String estadoInicialComentario(String comentario) {
        return comentario != null && !comentario.isBlank() ? PENDIENTE : null;
    }
}
