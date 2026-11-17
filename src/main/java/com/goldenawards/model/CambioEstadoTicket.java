package com.goldenawards.model;

import java.time.LocalDateTime;

/** Una entrada del historial de un ticket: a que estado paso, cuando y quien lo hizo (C-05). */
public class CambioEstadoTicket {

    private String estado;
    private LocalDateTime fecha;
    private String responsable;   // username de quien hizo el cambio

    public CambioEstadoTicket() {}

    public CambioEstadoTicket(String estado, LocalDateTime fecha, String responsable) {
        this.estado = estado;
        this.fecha = fecha;
        this.responsable = responsable;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
}
