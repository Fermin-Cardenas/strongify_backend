package com.app.demo.DTO.Response;

import java.time.LocalDateTime;

public class ProgresoResponse {
    private Long id;
    private Double peso;
    private Double imc;
    private LocalDateTime fecha;

    public ProgresoResponse() {}

    public ProgresoResponse(Long id, Double peso, Double imc, LocalDateTime fecha) {
        this.id = id;
        this.peso = peso;
        this.imc = imc;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}

