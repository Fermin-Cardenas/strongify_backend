package com.app.demo.Entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "agenda_clases")
public class AgendaClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la tabla clases_catalogo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_catalogo_id", nullable = false)
    private Catalogo catalogo;

    // Relación con el coach (usuarios.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", referencedColumnName = "user_id")
    private User coach;

    // Relación con la sucursal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", referencedColumnName = "sucursal_id")
    private Sucursal sucursal;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private OffsetDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private OffsetDateTime fechaHoraFin;

    @Column(name = "cupo_actual")
    private Integer cupoActual;

    // Constructor vacío (requerido por JPA)
    public AgendaClase() {}

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public User getCoach() {
        return coach;
    }

    public void setCoach(User coach) {
        this.coach = coach;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public OffsetDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(OffsetDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public OffsetDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(OffsetDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public Integer getCupoActual() {
        return cupoActual;
    }

    public void setCupoActual(Integer cupoActual) {
        this.cupoActual = cupoActual;
    }
}
