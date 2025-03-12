package com.example.demo.Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Exceptions.ResourceNotFoundException;

import jakarta.validation.Valid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestion de usuarios y autorizados")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios registrados en el sistema.")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers()
                .stream()
                .map(user -> new UserDTO(user.getNombre(), user.getEmail(), user.getFechaRegistro()))
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios registrados en el sistema.");
        }

        return ResponseEntity.ok(users);
    }
    
    
    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve los detalles de un usuario específico basado en su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID del usuario a buscar", required = true) @PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado."));
        
        UserDTO userDTO = new UserDTO(user.getNombre(), user.getEmail(), user.getFechaRegistro());
        return ResponseEntity.ok(userDTO);
    }
    
    
    
    

    @Operation(summary = "Agregar un nuevo usuario", description = "Permite crear un nuevo usuario en el sistema.")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo crear el usuario: " + e.getMessage());
        }
    }  
     

    @Operation(summary = "Actualizar un usuario", description = "Permite actualizar los datos de un usuario específico basado en su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID del usuario a actualizar", required = true) @PathVariable Long id,
            @RequestBody User userDetails,
            @Parameter(description = "ID del usuario que realiza la operación", required = true) Long userId) {
    	
        if (!id.equals(userId)) {
            throw new IllegalArgumentException("El ID del usuario a modificar y el ID del usuario que realiza la operación debe ser el mismo.");
        }

        // Verificar que el userId no sea nulo
        if (userId == null) {
            return ResponseEntity.badRequest().body("El ID del usuario que realiza la operación no puede estar vacío.");
        }

        // Verificar que el usuario exista
        if (!userService.existsUserById(userId)) {
        	throw new ResourceNotFoundException("El usuario con ID " + userId + " no existe.");
        }

        // Intentar actualizar el usuario
        try {
        	User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en los datos proporcionados: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ocurrió un error al actualizar el usuario: " + e.getMessage());
        }
    }


    
    
    @Operation(summary = "Eliminar un usuario", description = "Permite eliminar un usuario específico basado en su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@Parameter(description = "ID del usuario a eliminar", required = true) @PathVariable Long id, @RequestParam boolean confirm) {
        if (!confirm) {
        	throw new IllegalArgumentException("La confirmación explícita es requerida para eliminar la cuenta.");
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Cuenta eliminada exitosamente. Se ha enviado un correo de confirmación.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    
    
    
    @Operation(summary = "Añaidr un autorizado", description = "Permite que un usuario añada autorizados para recoger sus pedidos.")
    @PostMapping("/{tokenId}/{userId}/{addressAuth}/autorizados")
    public ResponseEntity<?> agregarAutorizado(
        @Parameter(description = "ID del token", required = true) @PathVariable Long tokenId,
        @Parameter(description = "ID del usuario para añadir un autorizado", required = true) @PathVariable Long userId,
        @Parameter(description = "Adress del autorizado", required = true) @PathVariable String addressAuth,
        @RequestBody Autorizado autorizado) {
        try {
            if (autorizado.getPedido() == null) {
            	throw new IllegalArgumentException("El campo 'pedido' no puede ser nulo.");
            }
            Autorizado nuevoAutorizado = userService.agregarAutorizado(userId, autorizado);
            añadirAutorizado(tokenId, addressAuth);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAutorizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al agregar autorizado: " + e.getMessage());
        }
    }


    //Probar
    private void añadirAutorizado(Long tokenId, String addressAuth) {
        try {
            String apiUrl = "http://localhost:3001/transfer-authorization";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format("{\"tokenId\":%d,\"newAuthorized\":\"%s\"}", tokenId, addressAuth);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Leer la respuesta de la API
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response: " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    @Operation(summary = "Listar personas autorizadas", description = "Permite listar las personas autorizadas de un usuario")
    @GetMapping("/{userId}/autorizados")
    public ResponseEntity<?> listarAutorizados(@Parameter(description = "ID del usuario a mostrar los autorizados", required = true) @PathVariable Long userId) {
        List<Autorizado> autorizados = userService.listarAutorizados(userId);
        if (autorizados.isEmpty()) {
        	throw new ResourceNotFoundException("No se encontraron personas autorizadas para el usuario con ID " + userId);
        }
        return ResponseEntity.ok(autorizados);
    }
    
    
    

    @Operation(summary = "Modificar persona autorizada", description = "Permite modificar las personas autorizadas de un usuario")
    @PutMapping("/{userId}/autorizados/{autorizadoId}")
    public ResponseEntity<?> modificarAutorizado(
        @Parameter(description = "ID del usuario para modificar un autorizado suyo", required = true) @PathVariable Long userId,
        @Parameter(description = "ID del autorizado para modificar sus datos", required = true) @PathVariable Long autorizadoId,
        @RequestBody Autorizado autorizadoDetalles) {
        try {
            Autorizado actualizado = userService.modificarAutorizado(userId, autorizadoId, autorizadoDetalles);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("No se pudo encontrar o actualizar el autorizado: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar persona autorizada", description = "Permite eliminar las personas autorizadas de un usuario")
    @DeleteMapping("/{userId}/autorizados/{autorizadoId}")
    public ResponseEntity<String> eliminarAutorizado(
    	@Parameter(description = "ID del usuario para eliminar un autorizado suyo", required = true) @PathVariable Long userId,
    	@Parameter(description = "ID del autorizado para eliminar sus datos", required = true) @PathVariable Long autorizadoId) {
        userService.eliminarAutorizado(userId, autorizadoId);
        return ResponseEntity.ok("Persona autorizada eliminada exitosamente");
    }
    
    @Operation(summary = "Verificar si un usuario es administrador", description = "Verifica si un usuario tiene privilegios de administrador.")
    @GetMapping("/{id}/isAdmin")
    public ResponseEntity<Boolean> esUsuarioAdmin(
            @Parameter(description = "ID del usuario a verificar", required = true) @PathVariable Long id) {
        boolean esAdmin = userService.isUserAdmin(id);
        return new ResponseEntity<>(esAdmin, HttpStatus.OK);
    }
}
