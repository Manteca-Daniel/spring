package com.example.demo.Pedidos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.Empresas.EmpresaService;
import com.example.demo.Empresas.Repartidor;
import com.example.demo.Exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Operaciones relacionadas con la gestion de pedidos")
public class PedidoController {

	
	private String cid;
    @Autowired
    private PedidoService pedidosService;

    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private RestTemplate restTemplate; // Aquí usamos RestTemplate

    @Operation(summary = "Agregar un pedido", description = "Agrega un nuevo pedido al sistema")
    @PostMapping
    public ResponseEntity<Pedido> agregarPedido(@Valid @RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidosService.agregarPedido(pedido);

        String url = "http://localhost:3000/api/v1/pedidos";
        Map<String, Object> body = new HashMap<>();
        body.put("cadena", "Pedido" + nuevoPedido.getId().toString());
        body.put("datos", nuevoPedido);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("upload")) {
                    Map<String, Object> uploadData = (Map<String, Object>) responseBody.get("upload");
                    if (uploadData.containsKey("IpfsHash")) {
                        cid = (String) uploadData.get("IpfsHash");
                        System.out.println("CID recibido: " + cid);
                    } else {
                        System.err.println("La respuesta no contiene 'IpfsHash'");
                    }
                } else {
                    System.err.println("La respuesta no contiene 'upload'");
                }
            } else {
                System.err.println("Error al enviar el pedido a Node.js: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error en la comunicación con la API de Node.js: " + e.getMessage());
        }

        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    
    @GetMapping("/recuperarMetadata/{cid}")
    public ResponseEntity<String> recuperarMetadata(@PathVariable String cid) {
        // URL del endpoint de Node.js con el CID como parámetro
        String url = "http://localhost:3000/api/v1/pedidos/" + cid;
        
        try {
            // Realizar la petición GET
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,  // No necesitamos cuerpo para una petición GET
                String.class  // Tipo de la respuesta que esperamos (String en este caso)
            );

            // Retornar la respuesta recibida directamente
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());

        } catch (Exception e) {
            // Manejo de errores
            System.err.println("Error en la comunicación con la API de Node.js: " + e.getMessage());
            return new ResponseEntity<>("Error al recuperar los datos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Operation(summary = "Obtener todos los pedidos", description = "Recupera un listado de todos los pedidos registrados")
    @GetMapping
    public ResponseEntity<Map<Long, Pedido>> obtenerPedidos() {
        Map<Long, Pedido> pedidos = pedidosService.obtenerPedidos();
        if (pedidos == null || pedidos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron pedidos registrados.");
        }
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un pedido por ID", description = "Recupera los datos de un pedido específico usando su ID")
    @GetMapping("/{id}")
    public Pedido obtenerPedidoPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidosService.obtenerPedidoPorId(id);
        return pedido.orElseThrow(() -> new ResourceNotFoundException("No se encontró un pedido con el ID especificado."));
    }

    @Operation(summary = "Obtener pedidos por cliente", description = "Recupera todos los pedidos asociados a un cliente específico por su ID")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorIdCliente(@PathVariable Long idCliente) {
        List<Pedido> pedidos = pedidosService.obtenerPedidoPorIdCliente(idCliente);
        if (pedidos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron pedidos para el cliente con ID: " + idCliente);
        }
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Actualizar un pedido", description = "Actualiza los datos de un pedido existente")
    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable Long id, @Valid @RequestBody Pedido pedidoActualizado) {
        Optional<Pedido> pedido = pedidosService.actualizarPedido(id, pedidoActualizado);
        return pedido.orElseThrow(() -> new ResourceNotFoundException("El pedido con ID " + id + " no fue encontrado."));
    }

    @Operation(summary = "Eliminar un pedido", description = "Elimina un pedido existente por su ID")
    @DeleteMapping("/cliente/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        if (!pedidosService.eliminarPedido(id)) {
            throw new ResourceNotFoundException("El pedido con ID " + id + " no fue encontrado.");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Asignar repartidor a un pedido", description = "Asocia un repartidor específico a un pedido existente")
    @PostMapping("/{pedidoId}/asignarRepartidor")
    public ResponseEntity<String> asignarRepartidor(
            @PathVariable Long pedidoId,
            @Parameter(description = "ID del repartidor a asignar", required = true) @RequestParam Long repartidorId,
            @Parameter(description = "ID de la empresa del repartidor", required = true) @RequestParam Long empresaId) {
        Optional<Pedido> pedidoOpt = pedidosService.obtenerPedidoPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new ResourceNotFoundException("Pedido no encontrado.");
        }

        Repartidor repartidor = empresaService.obtenerRepartidorPorId(empresaId, repartidorId);
        if (repartidor == null) {
            throw new IllegalArgumentException("Repartidor no válido.");
        }

        boolean asignado = pedidosService.asignarRepartidor(pedidoId, repartidorId);
        if (!asignado) {
            throw new RuntimeException("Error al asignar el repartidor.");
        }

        empresaService.notificarRepartidor(repartidorId, "Se te ha asignado un nuevo pedido: " + pedidoId);
        return ResponseEntity.ok("Repartidor asignado correctamente.");
    }

    @Operation(summary = "Actualizar el estado de un pedido", description = "Modifica el estado de un pedido existente")
    @PutMapping("/{id}/estado")
    public ResponseEntity<String> actualizarEstadoPedido(
            @PathVariable Long id,
            @Parameter(description = "ID del repartidor que actualiza el estado", required = true) @RequestParam Long repartidorId,
            @Parameter(description = "Nuevo estado del pedido", required = true) @RequestParam String nuevoEstado) {
        boolean actualizado = pedidosService.actualizarEstadoPedido(id, repartidorId, nuevoEstado);
        if (!actualizado) {
            throw new IllegalArgumentException("No se pudo actualizar el estado del pedido.");
        }
        return ResponseEntity.ok("Estado del pedido actualizado a: " + nuevoEstado);
    }
}
