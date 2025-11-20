package com.app.demo.DTO.Response;

import java.time.OffsetDateTime;

public class ReservaResponse {
    private Long id;
    private ClienteInfo cliente;
    private ClaseAgendadaInfo claseAgendada;
    private OffsetDateTime fechaReserva;
    private String estado;
    private Boolean asistencia;

    public static class ClienteInfo {
        private Long id;
        private String username;

        public ClienteInfo() {}

        public ClienteInfo(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public static class ClaseAgendadaInfo {
        private Long id;
        private String nombre;

        public ClaseAgendadaInfo() {}

        public ClaseAgendadaInfo(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public ReservaResponse() {}

    public ReservaResponse(Long id, ClienteInfo cliente, ClaseAgendadaInfo claseAgendada, 
                          OffsetDateTime fechaReserva, String estado, Boolean asistencia) {
        this.id = id;
        this.cliente = cliente;
        this.claseAgendada = claseAgendada;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.asistencia = asistencia;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ClienteInfo getCliente() { return cliente; }
    public void setCliente(ClienteInfo cliente) { this.cliente = cliente; }

    public ClaseAgendadaInfo getClaseAgendada() { return claseAgendada; }
    public void setClaseAgendada(ClaseAgendadaInfo claseAgendada) { this.claseAgendada = claseAgendada; }

    public OffsetDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(OffsetDateTime fechaReserva) { this.fechaReserva = fechaReserva; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getAsistencia() { return asistencia; }
    public void setAsistencia(Boolean asistencia) { this.asistencia = asistencia; }
}

