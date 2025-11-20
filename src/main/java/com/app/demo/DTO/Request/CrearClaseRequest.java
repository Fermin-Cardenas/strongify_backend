package com.app.demo.DTO.Request;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class CrearClaseRequest {
    private String nombreClase;
    private String descripcion;
    
    @JsonDeserialize(using = FlexibleDateTimeDeserializer.class)
    private OffsetDateTime fechaHoraInicio;
    
    @JsonDeserialize(using = FlexibleDateTimeDeserializer.class)
    private OffsetDateTime fechaHoraFin;
    private Integer duracionMinutos;
    private Integer cupoMaximo;
    private Long sucursalId;
    private Long catalogoId;

    public CrearClaseRequest() {}

    public String getNombreClase() {
        return nombreClase;
    }

    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Long getCatalogoId() {
        return catalogoId;
    }

    public void setCatalogoId(Long catalogoId) {
        this.catalogoId = catalogoId;
    }
}

