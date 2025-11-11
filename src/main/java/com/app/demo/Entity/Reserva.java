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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "reservas",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cliente_id", "clase_agendada_id"})
    }
)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la tabla usuarios (cliente)
    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", nullable = false)
    private User cliente;

    // Relación con la tabla agenda_clases
    @ManyToOne
    @JoinColumn(name = "clase_agendada_id", referencedColumnName = "id", nullable = false)
    private AgendaClase claseAgendada;

    @Column(name = "fecha_reserva", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime fechaReserva;

    @Column(name = "estado", length = 50)
    private String estado = "CONFIRMADA";

    @Column(name = "asistencia")
    private Boolean asistencia = false;

    // ====== Getters y Setters ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCliente() {
        return cliente;
    }

    public void setCliente(User cliente) {
        this.cliente = cliente;
    }

    public AgendaClase getClaseAgendada() {
        return claseAgendada;
    }

    public void setClaseAgendada(AgendaClase claseAgendada) {
        this.claseAgendada = claseAgendada;
    }

    public OffsetDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(OffsetDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Boolean asistencia) {
        this.asistencia = asistencia;
    }
}
