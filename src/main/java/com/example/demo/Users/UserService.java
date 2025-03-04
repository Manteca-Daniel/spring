package com.example.demo.Users;

import com.example.demo.Pedidos.Pedido;
import com.example.demo.Pedidos.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PedidoService pedidoService;

    private List<User> userList = new ArrayList<>();
    private Long nextId = 1L; // Para simular un ID autoincremental

    public List<User> getAllUsers() {
        return new ArrayList<>(userList);
    }

    public Optional<User> getUserById(Long id) {
        return userList.stream().filter(user -> user.getId().equals(id)).findFirst();
    }
    
    public Optional<User> getUserByEmail(String Email) {
        return userList.stream().filter(user -> user.getEmail().equals(Email)).findFirst();
    }
    
    
    
    
    

    public User createUser(User user) {
        try {
            if (!isValidPassword(user.getContrasena())) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 5 caracteres y contener al menos un número.");
            }
            if (!isEmailUnique(user.getEmail(), null)) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso.");
            }
            user.setId(nextId++);
            user.setFechaRegistro(LocalDate.now());
            userList.add(user);
            return user;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear el usuario: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado al crear el usuario.", e);
        }
    }

    
    
    
    
    
    public User updateUser(Long id, User userDetails) {
        try {
            Optional<User> existingUserOpt = getUserById(id);
            if (existingUserOpt.isEmpty()) {
                throw new RuntimeException("User not found with id " + id);
            }

            User existingUser = existingUserOpt.get();

            if (!isValidPassword(userDetails.getContrasena())) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 5 caracteres y contener al menos un número.");
            }

            if (!isEmailUnique(userDetails.getEmail(), id)) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso.");
            }

            existingUser.setNombre(userDetails.getNombre());
            existingUser.setContrasena(userDetails.getContrasena());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setAddress(userDetails.getAddress());

            if (userDetails.getAutorizados() != null) {
                List<Autorizado> validAutorizados = new ArrayList<>();
                for (Autorizado autorizado : userDetails.getAutorizados()) {
                    if (getUserById(autorizado.getIdAutorizado()).isPresent()) {
                        Optional<Pedido> pedidoOpt = pedidoService.obtenerPedidoPorId(autorizado.getPedido().getId());
                        if (pedidoOpt.isPresent()) {
                            autorizado.setPedido(pedidoOpt.get());
                            validAutorizados.add(autorizado);
                        } else {
                            throw new RuntimeException("El ID del pedido " + autorizado.getPedido().getId() + " no existe.");
                        }
                    } else {
                        throw new RuntimeException("El ID autorizado " + autorizado.getIdAutorizado() + " no existe.");
                    }
                }
                existingUser.setAutorizados(validAutorizados);
            }

            return existingUser;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar el usuario: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado al actualizar el usuario.", e);
        }
    }

    public void deleteUser(Long id) {
        Optional<User> userOpt = getUserById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("El usuario con id " + id + " no existe.");
        }

        User user = userOpt.get();

        // Eliminar autorizados y datos relacionados
        if (user.getAutorizados() != null) {
            user.getAutorizados().clear();
        }

        // Eliminar pedidos asociados
        List<Pedido> pedidosAsociados = pedidoService.obtenerPedidosPorUsuario(user.getId());
        for (Pedido pedido : pedidosAsociados) {
            pedidoService.eliminarPedido(pedido.getId());
        }

        // Eliminar usuario de la lista
        userList.remove(user);

        // Enviar correo de confirmación
        enviarCorreoConfirmacionEliminacion(user.getEmail(), user.getNombre());
    }
    
    public Autorizado agregarAutorizado(Long userId, Autorizado autorizado) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el autorizado no sea duplicado
        if (user.getAutorizados().stream().anyMatch(a -> a.getIdentificacion().equals(autorizado.getIdentificacion()))) {
            throw new IllegalArgumentException("El autorizado ya existe para este usuario");
        }

        // Validar que Ethereum Address no sea nula
        if (autorizado.getEthereumAddress() == null || autorizado.getEthereumAddress().isEmpty()) {
            throw new IllegalArgumentException("La dirección Ethereum es obligatoria");
        }

        // Obtener el ID más alto de los autorizados actuales y asignar el siguiente ID
        Long maxId = user.getAutorizados().stream()
                         .mapToLong(Autorizado::getIdAutorizado)
                         .max()
                         .orElse(0L);  // Si no hay autorizados, se usará 0 como valor inicial

        autorizado.setIdAutorizado(maxId + 1); // Asignar el siguiente ID
        user.getAutorizados().add(autorizado);
        return autorizado;
    }


    // Listar todas las personas autorizadas
    public List<Autorizado> listarAutorizados(Long userId) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getAutorizados();
    }

    // Modificar una persona autorizada
    public Autorizado modificarAutorizado(Long userId, Long autorizadoId, Autorizado autorizadoDetalles) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Autorizado autorizado = user.getAutorizados().stream()
            .filter(a -> a.getIdAutorizado().equals(autorizadoId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Persona autorizada no encontrada"));

        autorizado.setNombreCompleto(autorizadoDetalles.getNombreCompleto());
        autorizado.setTelefono(autorizadoDetalles.getTelefono());
        autorizado.setEthereumAddress(autorizadoDetalles.getEthereumAddress());
        return autorizado;
    }

    // Eliminar una persona autorizada
    public void eliminarAutorizado(Long userId, Long autorizadoId) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.getAutorizados().removeIf(a -> a.getIdAutorizado().equals(autorizadoId));
    }

    private void enviarCorreoConfirmacionEliminacion(String email, String nombre) {
        // Implementa tu lógica para enviar un correo electrónico aquí
        System.out.println("Correo enviado a " + email + " confirmando la eliminación de la cuenta.");
    }


    // Método auxiliar para validar la contraseña
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 5 && password.matches(".*\\d.*");
    }

    // Método auxiliar para verificar que el correo sea único
    private boolean isEmailUnique(String email, Long idToExclude) {
        return userList.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .allMatch(user -> idToExclude != null && user.getId().equals(idToExclude));
    }
    
    public boolean isUserAdmin(Long userId) {
        return getUserById(userId)
                .map(User::isAdmin)
                .orElse(false);
    }

    public boolean existsUserById(Long id) {
        return userList.stream().anyMatch(user -> user.getId().equals(id));
    }
    
}
