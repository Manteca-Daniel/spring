package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.Users.UserDataLoader;

@SpringBootApplication
public class ProyectoAutoRizameApplication {
	
	UserDataLoader us=new UserDataLoader(null);

    public static void main(String[] args) {
        SpringApplication.run(ProyectoAutoRizameApplication.class, args);
        System.out.println("Spring ejecutado con éxito");
    }
    
    private static void bbdd() {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "1234";
        String dbName = "autorizame";
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            
            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            statement.executeUpdate(sql);
            System.out.println("Base de datos '" + dbName + "' creada o ya existente");
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            return;
        }

        String dbUrl = url + dbName;
        try (Connection connection = DriverManager.getConnection(dbUrl, user, password);
             Statement statement = connection.createStatement()) {
            
        	String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
        	        + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
        	        + "nombre VARCHAR(255) NOT NULL, "
        	        + "contrasena VARCHAR(255) NOT NULL, "
        	        + "email VARCHAR(255) NOT NULL UNIQUE, "
        	        + "address VARCHAR(255), "
        	        + "admin BOOLEAN NOT NULL, "
        	        + "fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            statement.executeUpdate(createTableSQL);
            System.out.println("Tabla 'users' creada o ya existente");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla 'users': " + e.getMessage());
        }
    }
}
