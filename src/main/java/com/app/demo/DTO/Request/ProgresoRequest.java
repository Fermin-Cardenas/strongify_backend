package com.app.demo.DTO.Request;

public class ProgresoRequest {
    private Double peso;

    public ProgresoRequest() {}

    public ProgresoRequest(Double peso) {
        this.peso = peso;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
}

