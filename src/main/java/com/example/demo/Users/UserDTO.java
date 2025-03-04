package com.example.demo.Users;

import java.time.LocalDate;

public class UserDTO {
    private String nombre;
    private String email;
    private LocalDate fechaRegistro;

    public UserDTO(String nombre, String email, LocalDate fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
}
