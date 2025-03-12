package com.example.demo.Users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.Pedidos.Pedido;

@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserService userService;

    public UserDataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
    	bbdd();
        if (userService.getAllUsers().isEmpty()) {
            User usuarioPorDefecto = new User();
            usuarioPorDefecto.setNombre("Daniel Manteca");
            usuarioPorDefecto.setContrasena("password1234");
            usuarioPorDefecto.setEmail("danielmanteca.dmg1@gmail.com");
            usuarioPorDefecto.setAddress("0x123456789abcdef123456789abcdef12345678");
            usuarioPorDefecto.setAdmin(true);

            // Crea un pedido por defecto con ID 1
            Pedido pedidoPorDefecto = new Pedido();
            pedidoPorDefecto.setId(1L); // Establece el ID del pedido como 1
            pedidoPorDefecto.setDescripcion("Pedido de prueba por defecto");

            // Crea un autorizado por defecto y le asigna el pedido
            Autorizado autorizadoPorDefecto = new Autorizado();
            autorizadoPorDefecto.setIdAutorizado(1L); // Asigna un ID al autorizado
            autorizadoPorDefecto.setNombreCompleto("Autorizado por defecto");
            autorizadoPorDefecto.setEthereumAddress("0DSJKDHAUDWIOUISDJOIWEWQDKA");
            autorizadoPorDefecto.setIdentificacion("12345678");
            autorizadoPorDefecto.setPedido(pedidoPorDefecto); // Asigna el pedido completo al autorizado
            autorizadoPorDefecto.setTelefono("662639949");

            // Añade el autorizado a la lista de autorizados
            List<Autorizado> autorizados = new ArrayList<>();
            autorizados.add(autorizadoPorDefecto);
            usuarioPorDefecto.setAutorizados(autorizados);

            // Guarda el usuario con el autorizado
            userService.createUser(usuarioPorDefecto);

            System.out.println("Datos de prueba cargados: Usuario inicial con autorizado y pedido creado.");
        }
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
