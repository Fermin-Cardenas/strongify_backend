package com.app.demo.DTO.Response;

import java.time.OffsetDateTime;

public class ClaseResponse {
    private Long id;
    private String nombreClase;
    private String descripcion;
    private Long coachId;
    private String coachNombre;
    private Long sucursalId;
    private String sucursalNombre;
    private OffsetDateTime fechaHoraInicio;
    private OffsetDateTime fechaHoraFin;
    private Integer cupoActual;
    private Integer cupoMaximo;
    private Integer duracionMinutos;
    private CatalogoInfo catalogo;

    public static class CatalogoInfo {
        private Long id;
        private String nombre;
        private String descripcion;

        public CatalogoInfo() {}

        public CatalogoInfo(Long id, String nombre, String descripcion) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public ClaseResponse() {}

    public ClaseResponse(Long id, String nombreClase, String descripcion, Long coachId, String coachNombre,
                        Long sucursalId, String sucursalNombre, OffsetDateTime fechaHoraInicio,
                        OffsetDateTime fechaHoraFin, Integer cupoActual, Integer cupoMaximo, Integer duracionMinutos) {
        this.id = id;
        this.nombreClase = nombreClase;
        this.descripcion = descripcion;
        this.coachId = coachId;
        this.coachNombre = coachNombre;
        this.sucursalId = sucursalId;
        this.sucursalNombre = sucursalNombre;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.cupoActual = cupoActual;
        this.cupoMaximo = cupoMaximo;
        this.duracionMinutos = duracionMinutos;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreClase() { return nombreClase; }
    public void setNombreClase(String nombreClase) { this.nombreClase = nombreClase; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachNombre() { return coachNombre; }
    public void setCoachNombre(String coachNombre) { this.coachNombre = coachNombre; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public String getSucursalNombre() { return sucursalNombre; }
    public void setSucursalNombre(String sucursalNombre) { this.sucursalNombre = sucursalNombre; }

    public OffsetDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(OffsetDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public OffsetDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(OffsetDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    public Integer getCupoActual() { return cupoActual; }
    public void setCupoActual(Integer cupoActual) { this.cupoActual = cupoActual; }

    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public CatalogoInfo getCatalogo() { return catalogo; }
    public void setCatalogo(CatalogoInfo catalogo) { this.catalogo = catalogo; }
}

