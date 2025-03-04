package com.example.demo.Pedidos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.Users.Autorizado;

@Component
public class PedidoDataLoader implements CommandLineRunner {

    private final PedidoService pedidoService;

    public PedidoDataLoader(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Override
    public void run(String... args) throws Exception {
    	
//    	if(pedidoService.obtenerPedidos().isEmpty()) {
//	        Destinatario destinatario = new Destinatario();
//	        destinatario.setId(1L);
//	        destinatario.setNombre("Daniel Manteca");
//	
//	        // Crear el autorizado
//	        Autorizado autorizado = new Autorizado();
//	        autorizado.setIdAutorizado(1L);
//	
//	        // Crear el pedido
//	        Pedido pedido = new Pedido();
//	        pedido.setDescripcion("Entrega de paquete frágil");
//	        pedido.setEstado("Pendiente");
//	        pedido.setDestinatario(destinatario);
//	        pedido.setDireccion("Calle Falsa 123, Ciudad");
//	        pedido.setAutorizado(autorizado);
//	        pedido.setRepartidorId(3L);
//	        pedido.agregarEstadoHistorial("Pendiente");
//	
//	        // Guardar el pedido
//	        pedidoService.agregarPedido(pedido);
//	
//	        System.out.println("Datos de prueba cargados: Pedido inicial creado.");
//    	}
    	
        
    }
}