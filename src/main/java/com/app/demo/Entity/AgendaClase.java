package com.app.demo.Entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agenda_clases")
public class AgendaClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la tabla clases_catalogo
    @Column(name = "clase_catalogo_id")
    private Integer claseCatalogoId;

    // Relación con el coach (usuarios.id)
    @ManyToOne
    @JoinColumn(name = "coach_id", referencedColumnName = "user_id")
    private User coach;

    // Relación con la sucursal
    @Column(name = "sucursal_id")
    private Integer sucursalId;

    @Column(name = "fecha_hora_inicio")
    private OffsetDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private OffsetDateTime fechaHoraFin;

    @Column(name = "cupo_actual")
    private Integer cupoActual;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getClaseCatalogoId() { return claseCatalogoId; }
    public void setClaseCatalogoId(Integer claseCatalogoId) { this.claseCatalogoId = claseCatalogoId; }

    public User getCoach() { return coach; }
    public void setCoach(User coach) { this.coach = coach; }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public OffsetDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(OffsetDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public OffsetDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(OffsetDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    public Integer getCupoActual() { return cupoActual; }
    public void setCupoActual(Integer cupoActual) { this.cupoActual = cupoActual; }
}
