package com.example.demo.Pedidos;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.Users.Autorizado;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Pedido {
    private Long id;
    private String descripcion;
    private String estado;
    private Destinatario destinatario;
    private String direccion;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Solo incluir si no es null
    private Autorizado autorizado;

    private Long repartidorId;
    private List<String> historialEstados = new ArrayList<>();

    // Constructor, Getters y Setters
    public Pedido() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Destinatario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Destinatario destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Autorizado getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(Autorizado autorizado) {
        this.autorizado = autorizado;
    }

    public Long getRepartidorId() {
        return repartidorId;
    }

    public void setRepartidorId(Long repartidorId) {
        this.repartidorId = repartidorId;
    }

    public List<String> getHistorialEstados() {
        return historialEstados;
    }

    public void agregarEstadoHistorial(String estado) {
        historialEstados.add(estado);
    }
}
