package com.app.demo.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "membresia_activa")
public class MembresiaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membresia_id")
    private Long membresiaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @Column(nullable = false)
    private Tipo tipo;

    @Column(nullable = false)
    private Double costo;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracion; // Duración acumulada total

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activa = true;

    public MembresiaActiva() {}

    public MembresiaActiva(Long membresiaId, User usuario, Tipo tipo, Double costo, 
                          Integer duracion, LocalDate fechaInicio, LocalDate fechaFin, Boolean activa) {
        this.membresiaId = membresiaId;
        this.usuario = usuario;
        this.tipo = tipo;
        this.costo = costo;
        this.duracion = duracion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
    }

    // Getters y Setters
    public Long getMembresiaId() {
        return membresiaId;
    }

    public void setMembresiaId(Long membresiaId) {
        this.membresiaId = membresiaId;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean isActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}

