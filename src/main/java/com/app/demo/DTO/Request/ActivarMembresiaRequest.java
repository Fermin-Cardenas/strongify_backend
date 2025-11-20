package com.app.demo.DTO.Request;

public class ActivarMembresiaRequest {
    private String tipo;
    private Double costo;
    private Integer duracion;

    public ActivarMembresiaRequest() {}

    public ActivarMembresiaRequest(String tipo, Double costo, Integer duracion) {
        this.tipo = tipo;
        this.costo = costo;
        this.duracion = duracion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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

