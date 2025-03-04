package com.example.demo.Users;

import com.example.demo.Pedidos.Pedido;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class User {
    private Long id;
    @NotNull
    private String nombre;
    @NotNull
    private String contrasena;
    @NotNull
    private String email;
    @NotNull
    private String address; // Ethereum address
    @NotNull
    private boolean admin;
    private LocalDate fechaRegistro; // Fecha de registro
    private List<Autorizado> autorizados; // Lista de autorizados con referencia al pedido

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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Autorizado> getAutorizados() {
        return autorizados;
    }

    public void setAutorizados(List<Autorizado> autorizados) {
        this.autorizados = autorizados;
    }
}