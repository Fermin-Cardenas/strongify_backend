package com.app.demo.DTO.Response;

public class AsistenciaResponse {

    private Long reservaId;
    private String nombreCliente;
    private String telefono;
    private Boolean asistencia;

    public AsistenciaResponse(Long reservaId, String nombreCliente, String telefono, Boolean asistencia) {
        this.reservaId = reservaId;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.asistencia = asistencia;
    }

    // Getters y Setters
    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getAsistencia() { return asistencia; }
    public void setAsistencia(Boolean asistencia) { this.asistencia = asistencia; }
}

