package com.app.demo.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "progreso_imc")
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progreso_id")
    private Long progresoId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "peso_kg")
    private Double peso;

    @Column(name = "imc")
    private Double imc;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public Progreso() {}

    public Progreso(Long progresoId, User user, Double peso, Double imc, LocalDateTime fechaRegistro) {
        this.progresoId = progresoId;
        this.user = user;
        this.peso = peso;
        this.imc = imc;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getProgresoId() {
        return progresoId;
    }

    public void setProgresoId(Long progresoId) {
        this.progresoId = progresoId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

}
