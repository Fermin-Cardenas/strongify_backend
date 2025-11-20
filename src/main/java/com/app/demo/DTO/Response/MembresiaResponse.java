package com.app.demo.DTO.Response;

import java.time.OffsetDateTime;

public class MembresiaResponse {
    private Long membresiaId;
    private String tipo;
    private Double costo;
    private Integer duracionDias;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private Long diasRestantes;
    private Boolean activa;

    public MembresiaResponse() {}

    public MembresiaResponse(Long membresiaId, String tipo, Double costo, Integer duracionDias,
                             OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Long diasRestantes, Boolean activa) {
        this.membresiaId = membresiaId;
        this.tipo = tipo;
        this.costo = costo;
        this.duracionDias = duracionDias;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasRestantes = diasRestantes;
        this.activa = activa;
    }

    // Getters y Setters
    public Long getMembresiaId() { return membresiaId; }
    public void setMembresiaId(Long membresiaId) { this.membresiaId = membresiaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }

    public OffsetDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(OffsetDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public OffsetDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(OffsetDateTime fechaFin) { this.fechaFin = fechaFin; }

    public Long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(Long diasRestantes) { this.diasRestantes = diasRestantes; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}

