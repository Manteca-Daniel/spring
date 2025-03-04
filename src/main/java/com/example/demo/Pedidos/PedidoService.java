package com.example.demo.Pedidos;
import com.example.demo.Users.User;
import com.example.demo.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class PedidoService {
   private Map<Long, Pedido> pedidos = new HashMap<>();
   private Long currentId = 1L;
   @Autowired
   private UserService userService; // Dependencia para acceder a los usuarios
   // Método para agregar un nuevo pedido
   public Pedido agregarPedido(Pedido pedido) {
	    try {
	        if (pedido.getDestinatario() == null || pedido.getDestinatario().getId() == null) {
	            throw new IllegalArgumentException("El destinatario debe ser válido y tener un ID.");
	        }
	        Optional<User> destinatarioOpt = userService.getUserById(pedido.getDestinatario().getId());
	        if (!destinatarioOpt.isPresent()) {
	            throw new IllegalArgumentException("El destinatario no existe en la base de datos de usuarios.");
	        }

	        if (pedido.getAutorizado() != null && pedido.getAutorizado().getIdAutorizado() != null) {
	            Optional<User> autorizadoOpt = userService.getUserById(pedido.getAutorizado().getIdAutorizado());
	            if (!autorizadoOpt.isPresent()) {
	                throw new IllegalArgumentException("El usuario autorizado no existe en la base de datos de usuarios.");
	            }
	        }

	        pedido.setId(currentId++);
	        pedidos.put(pedido.getId(), pedido);
	        return pedido;
	    } catch (IllegalArgumentException e) {
	        throw new RuntimeException("Error al agregar pedido: " + e.getMessage(), e);
	    } catch (Exception e) {
	        throw new RuntimeException("Ocurrió un error inesperado al agregar el pedido.", e);
	    }
	}
   // Método para obtener todos los pedidos
   public Map<Long, Pedido> obtenerPedidos() {
       return pedidos;
   }
   // Método para obtener un pedido por ID
   public Optional<Pedido> obtenerPedidoPorId(Long id) {
       return Optional.ofNullable(pedidos.get(id));
   }
  
// Método para obtener pedidos por cliente
   public List<Pedido> obtenerPedidoPorIdCliente(Long idCliente) {
       // Filtra los pedidos donde el destinatario tenga el ID proporcionado
       return pedidos.values().stream()
               .filter(pedido -> pedido.getDestinatario() != null &&
                                 pedido.getDestinatario().getId().equals(idCliente))
               .collect(Collectors.toList());
   }
   // Método para actualizar un pedido
   public Optional<Pedido> actualizarPedido(Long id, Pedido pedidoActualizado) {
       Pedido pedidoExistente = pedidos.get(id);
       if (pedidoExistente != null) {
           pedidoExistente.setDescripcion(pedidoActualizado.getDescripcion());
           pedidoExistente.setEstado(pedidoActualizado.getEstado());
           pedidoExistente.setDestinatario(pedidoActualizado.getDestinatario());
           pedidoExistente.setDireccion(pedidoActualizado.getDireccion());
           pedidoExistente.setAutorizado(pedidoActualizado.getAutorizado());
           return Optional.of(pedidoExistente);
       }
       return Optional.empty();
   }
   // Método para eliminar un pedido
   public boolean eliminarPedido(Long id) {
   	Pedido pedido = pedidos.get(id);
   	if (pedido != null) {
   		return pedidos.remove(id) != null;
   	}
   	return false;
   }
  
// Método para obtener pedidos por usuario
   public List<Pedido> obtenerPedidosPorUsuario(Long userId) {
       // Filtra los pedidos en función del destinatario
       return pedidos.values().stream()
               .filter(pedido -> pedido.getDestinatario() != null && pedido.getDestinatario().getId().equals(userId))
               .collect(Collectors.toList());
   }
  
   public boolean asignarRepartidor(Long pedidoId, Long repartidorId) {
       Pedido pedido = pedidos.get(pedidoId);
       System.out.print(pedido);
       if (pedido != null) {
           pedido.setRepartidorId(repartidorId);
           return true;
       }
       return false;
   }
  
   public boolean actualizarEstadoPedido(Long pedidoId, Long repartidorId, String nuevoEstado) {
       Pedido pedido = pedidos.get(pedidoId);
       // Verificar que el pedido exista y que el repartidor esté asignado
       if (pedido == null || !pedido.getRepartidorId().equals(repartidorId)) {
           throw new IllegalArgumentException("El repartidor no está autorizado para este pedido.");
       }
       // Verificar que el estado sea válido
       if (!List.of("Pendiente", "En proceso", "Entregado").contains(nuevoEstado)) {
           throw new IllegalArgumentException("Estado inválido. Los estados permitidos son: Pendiente, En proceso, Entregado.");
       }
       // Actualizar estado y agregar al historial
       pedido.setEstado(nuevoEstado);
       pedido.agregarEstadoHistorial(nuevoEstado);
       // Notificar al cliente
       notificarCliente(pedido.getDestinatario().getId(), "El estado de su pedido ha cambiado a: " + nuevoEstado);
       return true;
   }
  
   private void notificarCliente(Long clienteId, String mensaje) {
       // Simulación de una notificación al cliente
       System.out.println("Notificación al cliente " + clienteId + ": " + mensaje);
   }
}