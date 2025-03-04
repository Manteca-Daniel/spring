package com.example.demo.Empresas;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

public class Empresa {
    private Long id;
    @NotNull
    private String nombre;
    @NotNull
    private String telefono;
    @NotNull
    private String email;
    private LocalDate fechaRegistro;
    private Map<Long, Repartidor> repartidores = new HashMap<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Map<Long, Repartidor> getRepartidores() {
        return repartidores;
    }

    public void setRepartidores(Map<Long, Repartidor> repartidores) {
        this.repartidores = repartidores;
    }
}