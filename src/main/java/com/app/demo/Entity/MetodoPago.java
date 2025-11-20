package com.app.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "metodos_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_pago_id")
    private Long metodoPagoId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // "credito" | "debito"

    @Column(name = "ultimos_4", nullable = false, length = 4)
    private String ultimos4;

    @Column(name = "nombre_titular", nullable = false, length = 100)
    private String nombreTitular;

    @Column(name = "fecha_vencimiento", nullable = false, length = 5)
    private String fechaVencimiento; // "MM/YY"

    @Column(name = "marca", length = 20)
    private String marca; // "visa" | "mastercard" | "amex" | "other"

    @Column(name = "es_predeterminada", nullable = false)
    private Boolean esPredeterminada;

    public MetodoPago() {}

    public MetodoPago(Long metodoPagoId, User user, String tipo, String ultimos4, 
                     String nombreTitular, String fechaVencimiento, String marca, 
                     Boolean esPredeterminada) {
        this.metodoPagoId = metodoPagoId;
        this.user = user;
        this.tipo = tipo;
        this.ultimos4 = ultimos4;
        this.nombreTitular = nombreTitular;
        this.fechaVencimiento = fechaVencimiento;
        this.marca = marca;
        this.esPredeterminada = esPredeterminada;
    }

    // Getters y Setters
    public Long getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(Long metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Boolean getEsPredeterminada() {
        return esPredeterminada;
    }

    public void setEsPredeterminada(Boolean esPredeterminada) {
        this.esPredeterminada = esPredeterminada;
    }
}

