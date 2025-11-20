package com.app.demo.DTO.Response;

public class MetodoPagoResponse {
    private Long id;
    private String tipo;
    private String ultimos4;
    private String nombreTitular;
    private String fechaVencimiento;
    private Boolean esPredeterminada;
    private String marca;

    public MetodoPagoResponse() {}

    public MetodoPagoResponse(Long id, String tipo, String ultimos4, String nombreTitular, 
                             String fechaVencimiento, Boolean esPredeterminada, String marca) {
        this.id = id;
        this.tipo = tipo;
        this.ultimos4 = ultimos4;
        this.nombreTitular = nombreTitular;
        this.fechaVencimiento = fechaVencimiento;
        this.esPredeterminada = esPredeterminada;
        this.marca = marca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUltimos4() {
        return ultimos4;
    }

    public void setUltimos4(String ultimos4) {
        this.ultimos4 = ultimos4;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getEsPredeterminada() {
        return esPredeterminada;
    }

    public void setEsPredeterminada(Boolean esPredeterminada) {
        this.esPredeterminada = esPredeterminada;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
}

