package com.example.demo.Empresas;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class EmpresaService {
   private Map<Long, Empresa> empresas = new HashMap<>();
   private Long currentEmpresaId = 1L;
   private Long currentRepartidorId = 1L;
   public Empresa agregarEmpresa(Empresa empresa) {
	    try {
	        empresa.setId(currentEmpresaId++);
	        empresa.setFechaRegistro(LocalDate.now());
	        empresas.put(empresa.getId(), empresa);
	        return empresa;
	    } catch (Exception e) {
	        throw new RuntimeException("Error al agregar empresa: " + e.getMessage(), e);
	    }
	}
   public Repartidor agregarRepartidor(Long empresaId, Repartidor repartidor) {
	    try {
	        Optional<Empresa> empresaOpt = obtenerEmpresaPorId(empresaId);
	        if (empresaOpt.isEmpty()) {
	            throw new RuntimeException("La empresa con ID " + empresaId + " no existe.");
	        }

	        Empresa empresa = empresaOpt.get();
	        repartidor.setId(currentRepartidorId++);
	        repartidor.setFechaAlta(LocalDate.now());
	        empresa.getRepartidores().put(repartidor.getId(), repartidor);
	        return repartidor;
	    } catch (RuntimeException e) {
	        throw new RuntimeException("Error al agregar repartidor: " + e.getMessage(), e);
	    } catch (Exception e) {
	        throw new RuntimeException("Ocurrió un error inesperado al agregar repartidor.", e);
	    }
	}
   public Optional<Empresa> obtenerEmpresaPorId(Long id) {
       return Optional.ofNullable(empresas.get(id));
   }
   public Map<Long, Empresa> obtenerEmpresas() {
       return empresas;
   }
   public Optional<Empresa> actualizarEmpresa(Long id, Empresa empresaActualizada) {
       Empresa empresa = empresas.get(id);
       if (empresa != null) {
           empresa.setNombre(empresaActualizada.getNombre());
           empresa.setTelefono(empresaActualizada.getTelefono());
           empresa.setEmail(empresaActualizada.getEmail());
           empresa.setFechaRegistro(empresaActualizada.getFechaRegistro() != null ? empresaActualizada.getFechaRegistro() : empresa.getFechaRegistro());
           return Optional.of(empresa);
       }
       return Optional.empty();
   }
   public boolean eliminarEmpresa(Long id) {
       return empresas.remove(id) != null;
   }
  
   public List<Repartidor> obtenerRepartidores(Long empresaId) {
       Optional<Empresa> empresaOpt = obtenerEmpresaPorId(empresaId);
       if (empresaOpt.isPresent()) {
           Empresa empresa = empresaOpt.get();
           return new ArrayList<>(empresa.getRepartidores().values());
       }
       return null;
   }
  
   public Repartidor obtenerRepartidorPorId(Long empresaId, Long repartidorId) {
       Empresa empresa = empresas.get(empresaId);
       if (empresa != null) {
           return empresa.getRepartidores().get(repartidorId);
       }
       return null;
   }
   public Repartidor actualizarRepartidor(Long empresaId, Long repartidorId, Repartidor repartidorActualizado) {
       Optional<Empresa> empresaOpt = obtenerEmpresaPorId(empresaId);
       if (empresaOpt.isPresent()) {
           Empresa empresa = empresaOpt.get();
           Repartidor repartidor = empresa.getRepartidores().get(repartidorId);
           if (repartidor != null) {
               repartidor.setNombre(repartidorActualizado.getNombre());
               repartidor.setCorreo(repartidorActualizado.getCorreo());
               repartidor.setTelefono(repartidorActualizado.getTelefono());
               return repartidor;
           }
       }
       return null;
   }
   public boolean eliminarRepartidor(Long empresaId, Long repartidorId) {
       Optional<Empresa> empresaOpt = obtenerEmpresaPorId(empresaId);
       if (empresaOpt.isPresent()) {
           Empresa empresa = empresaOpt.get();
           return empresa.getRepartidores().remove(repartidorId) != null;
       }
       return false;
   }
  
   public boolean esRepartidorActivo(Long empresaId, Long repartidorId) {
       Empresa empresa = empresas.get(empresaId);
       if (empresa != null) {
           Repartidor repartidor = empresa.getRepartidores().get(repartidorId);
           return repartidor != null;
       }
       return false;
   }
  
   public void notificarRepartidor(Long repartidorId, String mensaje) {
       // Simulación de una notificación (esto puede ser un correo, SMS, etc.)
       System.out.println("Notificando al repartidor " + repartidorId + ": " + mensaje);
   }
}
