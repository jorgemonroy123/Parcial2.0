package com.goldenawards.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Propuesta de cambio del perfil de un jugador hecha por su Representante (REQ-REP-001).
 * Mientras esta PENDIENTE no modifica el perfil publico; solo al ser APROBADA por el Organizador
 * se copian estos valores al jugador (REQ-REP-002, C-05).
 */
@Document(collection = "propuestas_perfil")
public class PropuestaPerfil {

    @Id
    private String id;
    private String jugadorId;
    private String jugadorNombre;
    private String representanteId;
    private String representanteUsername;
    // valores propuestos
    private String biografia;
    private Integer partidos;
    private Integer goles;
    private Integer asistencias;
    private Integer trofeos;
    // estado: PENDIENTE -> APROBADA | RECHAZADA (con quien y cuando)
    private String estado;
    private LocalDateTime fecha;
    private String decididoPor;
    private LocalDateTime decididoEn;
    private String motivo;

    public PropuestaPerfil() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJugadorId() { return jugadorId; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }
    public String getJugadorNombre() { return jugadorNombre; }
    public void setJugadorNombre(String jugadorNombre) { this.jugadorNombre = jugadorNombre; }
    public String getRepresentanteId() { return representanteId; }
    public void setRepresentanteId(String representanteId) { this.representanteId = representanteId; }
    public String getRepresentanteUsername() { return representanteUsername; }
    public void setRepresentanteUsername(String representanteUsername) { this.representanteUsername = representanteUsername; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public Integer getPartidos() { return partidos; }
    public void setPartidos(Integer partidos) { this.partidos = partidos; }
    public Integer getGoles() { return goles; }
    public void setGoles(Integer goles) { this.goles = goles; }
    public Integer getAsistencias() { return asistencias; }
    public void setAsistencias(Integer asistencias) { this.asistencias = asistencias; }
    public Integer getTrofeos() { return trofeos; }
    public void setTrofeos(Integer trofeos) { this.trofeos = trofeos; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getDecididoPor() { return decididoPor; }
    public void setDecididoPor(String decididoPor) { this.decididoPor = decididoPor; }
    public LocalDateTime getDecididoEn() { return decididoEn; }
    public void setDecididoEn(LocalDateTime decididoEn) { this.decididoEn = decididoEn; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
