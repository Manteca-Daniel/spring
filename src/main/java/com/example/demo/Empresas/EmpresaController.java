package com.example.demo.Empresas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Exceptions.AccessDeniedException;
import com.example.demo.Exceptions.ResourceNotFoundException;
import com.example.demo.Users.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/empresas")
@Tag(name = "Empresas", description = "Operaciones relacionadas con la gestion de empresas y repartidores")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private UserService userService;
    
    @Operation(summary = "Agregar una empresa", description = "Agrega una empresa solo si eres administrador")
    @PostMapping
    public ResponseEntity<Empresa> agregarEmpresa(
            @Parameter(description = "ID del usuario que es admin", required = true) @RequestParam Long adminUserId,
            @Valid @RequestBody Empresa empresa) {
        if (!userService.isUserAdmin(adminUserId)) {
            throw new AccessDeniedException("Error: El usuario no tiene permisos para realizar esta acción.");
        }
        Empresa nuevaEmpresa = empresaService.agregarEmpresa(empresa);
        return new ResponseEntity<>(nuevaEmpresa, HttpStatus.CREATED);
    }


    @Operation(summary = "Obtener todas las empresas", description = "Recupera un listado de todas las empresas registradas")
    @GetMapping
    public ResponseEntity<Map<Long, Empresa>> obtenerEmpresas() {
        Map<Long, Empresa> empresas = empresaService.obtenerEmpresas();
        if (empresas == null || empresas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron empresas registradas.");
        }
        return new ResponseEntity<>(empresas, HttpStatus.OK);
    }

    @Operation(summary = "Obtener una empresa por ID", description = "Recupera los datos de una empresa específica usando su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtenerEmpresaPorId(@PathVariable Long id) {
        Optional<Empresa> empresa = empresaService.obtenerEmpresaPorId(id);
        return empresa.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una empresa con el ID especificado."));
    }

    @Operation(summary = "Actualizar una empresa", description = "Actualiza los datos de una empresa existente si eres administrador")
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(
            @PathVariable Long id,
            @Valid @RequestBody Empresa empresaActualizada,
            @Parameter(description = "ID del usuario que es admin", required = true) @RequestParam Long adminUserId) {
        if (!userService.isUserAdmin(adminUserId)) {
            throw new AccessDeniedException("El usuario no tiene permisos para realizar esta acción.");
        }
        Optional<Empresa> empresa = empresaService.actualizarEmpresa(id, empresaActualizada);
        return empresa.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una empresa con el ID especificado."));
    }

    @Operation(summary = "Eliminar una empresa", description = "Elimina una empresa existente si eres administrador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(
            @PathVariable Long id,
            @Parameter(description = "ID del usuario que es admin", required = true) @RequestParam Long adminUserId) {
        if (!userService.isUserAdmin(adminUserId)) {
            throw new AccessDeniedException("El usuario no tiene permisos para realizar esta acción.");
        }
        if (!empresaService.eliminarEmpresa(id)) {
            throw new ResourceNotFoundException("No se encontró una empresa con el ID especificado.");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Agregar un repartidor a una empresa", description = "Agrega un repartidor a una empresa específica")
    @PostMapping("/{empresaId}/repartidores")
    public ResponseEntity<Repartidor> agregarRepartidor(
            @PathVariable Long empresaId,
            @Valid @RequestBody Repartidor repartidor) {
        Repartidor nuevoRepartidor = empresaService.agregarRepartidor(empresaId, repartidor);
        if (nuevoRepartidor == null) {
            throw new ResourceNotFoundException("No se encontró una empresa con el ID especificado.");
        }
        return new ResponseEntity<>(nuevoRepartidor, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener repartidores de una empresa", description = "Recupera la lista de repartidores de una empresa específica")
    @GetMapping("/{empresaId}/repartidores")
    public ResponseEntity<List<Repartidor>> obtenerRepartidores(@PathVariable Long empresaId) {
        List<Repartidor> repartidores = empresaService.obtenerRepartidores(empresaId);
        if (repartidores == null || repartidores.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron repartidores para la empresa especificada.");
        }
        return new ResponseEntity<>(repartidores, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un repartidor", description = "Actualiza los datos de un repartidor específico si eres administrador")
    @PutMapping("/{empresaId}/repartidores/{repartidorId}")
    public ResponseEntity<Repartidor> actualizarRepartidor(
            @PathVariable Long empresaId,
            @PathVariable Long repartidorId,
            @Valid @RequestBody Repartidor repartidorActualizado,
            @Parameter(description = "ID del usuario que es admin", required = true) @RequestParam Long adminUserId) {
        if (!userService.isUserAdmin(adminUserId)) {
            throw new AccessDeniedException("El usuario no tiene permisos para realizar esta acción.");
        }
        Repartidor repartidor = empresaService.actualizarRepartidor(empresaId, repartidorId, repartidorActualizado);
        if (repartidor == null) {
            throw new ResourceNotFoundException("No se encontró el repartidor con el ID especificado en la empresa.");
        }
        return new ResponseEntity<>(repartidor, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un repartidor", description = "Elimina un repartidor de una empresa específica si eres administrador")
    @DeleteMapping("/{empresaId}/repartidores/{repartidorId}")
    public ResponseEntity<String> eliminarRepartidor(
            @PathVariable Long empresaId,
            @PathVariable Long repartidorId,
            @Parameter(description = "ID del usuario que es admin", required = true) @RequestParam Long adminUserId) {
        if (!userService.isUserAdmin(adminUserId)) {
            throw new AccessDeniedException("El usuario no tiene permisos para realizar esta acción.");
        }
        boolean eliminado = empresaService.eliminarRepartidor(empresaId, repartidorId);
        if (!eliminado) {
            throw new ResourceNotFoundException("Repartidor no encontrado.");
        }
        return ResponseEntity.ok("Repartidor eliminado exitosamente.");
    }
}