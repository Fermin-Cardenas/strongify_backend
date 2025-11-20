package com.app.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "membresias_tipos")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membresia_id")
    private Long membresiaId;

    private Tipo tipo;

    private Double costo;
    @Column(name = "duracion_dias")
    private Integer duracion;

    public Membresia() {}

    public Membresia(Long membresiaId, Tipo tipo, Double costo, Integer duracion) {
        this.membresiaId = membresiaId;
        this.tipo = tipo;
        this.costo = costo;
        this.duracion = duracion;
    }

    public Long getMembresiaId() {
        return membresiaId;
    }

    public void setMembresiaId(Long membresiaId) {
        this.membresiaId = membresiaId;
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

}
