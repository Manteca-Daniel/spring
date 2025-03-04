package com.example.demo.Empresas;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EmpresaDataLoader implements CommandLineRunner {

    private final EmpresaService empresaService;

    public EmpresaDataLoader(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (empresaService.obtenerEmpresas().isEmpty()) {
            Empresa empresa1 = new Empresa();
            empresa1.setNombre("TechCorp");
            empresa1.setTelefono("123456789");
            empresa1.setEmail("contacto@techcorp.com");

            Empresa empresaGuardada = empresaService.agregarEmpresa(empresa1);

            Repartidor repartidor1 = new Repartidor();
            repartidor1.setNombre("Julian Alvarez");
            repartidor1.setCorreo("julian.alvarez@techcorp.com");
            repartidor1.setTelefono("987654321");

            Repartidor repartidor2 = new Repartidor();
            repartidor2.setNombre("Lamine Yamal");
            repartidor2.setCorreo("lamine.yamal@techcorp.com");
            repartidor2.setTelefono("876543210");

            Repartidor repartidor3 = new Repartidor();
            repartidor3.setNombre("Vinicius Junior");
            repartidor3.setCorreo("vinicius.junior@techcorp.com");
            repartidor3.setTelefono("765432109");

            empresaService.agregarRepartidor(empresaGuardada.getId(), repartidor1);
            empresaService.agregarRepartidor(empresaGuardada.getId(), repartidor2);
            empresaService.agregarRepartidor(empresaGuardada.getId(), repartidor3);
            
            
            System.out.println("Datos de prueba cargados: Empresa inicial creada.");
        }
    }
}
