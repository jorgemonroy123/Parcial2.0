package com.goldenawards.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Ticket de soporte creado por cualquier rol (REQ-SOP-001). Estados: ABIERTO, EN_ATENCION, RESUELTO.
 * Solo guarda lo necesario (C-07): asunto, descripcion y quien lo creo.
 */
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;
    private String asunto;
    private String descripcion;
    private String estado;
    private String creadoPorId;
    private String creadoPorUsername;
    private String creadoPorRol;
    private LocalDateTime fechaCreacion;
    private List<CambioEstadoTicket> historial = new ArrayList<>();

    public Ticket() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCreadoPorId() { return creadoPorId; }
    public void setCreadoPorId(String creadoPorId) { this.creadoPorId = creadoPorId; }
    public String getCreadoPorUsername() { return creadoPorUsername; }
    public void setCreadoPorUsername(String creadoPorUsername) { this.creadoPorUsername = creadoPorUsername; }
    public String getCreadoPorRol() { return creadoPorRol; }
    public void setCreadoPorRol(String creadoPorRol) { this.creadoPorRol = creadoPorRol; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<CambioEstadoTicket> getHistorial() { return historial; }
    public void setHistorial(List<CambioEstadoTicket> historial) { this.historial = historial; }
}
