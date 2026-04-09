package com.empresa.gestionactivos.config;

import com.empresa.gestionactivos.model.PartidaPresupuestaria;
import com.empresa.gestionactivos.model.Proveedor;
import com.empresa.gestionactivos.model.Usuario;
import com.empresa.gestionactivos.repository.PartidaPresupuestariaRepository;
import com.empresa.gestionactivos.repository.ProveedorRepository;
import com.empresa.gestionactivos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Year;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final PartidaPresupuestariaRepository partidaRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            if (usuarioRepository.count() == 0) {
                usuarioRepository.save(buildUser("admin", "Administrador General", Usuario.RolUsuario.ADMINISTRADOR));
                usuarioRepository.save(buildUser("inventario", "Encargado Inventario", Usuario.RolUsuario.ENCARGADO_INVENTARIO));
                usuarioRepository.save(buildUser("compras", "Departamento Compras", Usuario.RolUsuario.DEPARTAMENTO_COMPRAS));
                usuarioRepository.save(buildUser("finanzas", "Gerencia Finanzas", Usuario.RolUsuario.GERENCIA_FINANZAS));
                usuarioRepository.save(buildUser("empleado", "Empleado Demo", Usuario.RolUsuario.EMPLEADO));
            }

            if (proveedorRepository.count() == 0) {
                Proveedor p = new Proveedor();
                p.setRfc("XAXX010101000");
                p.setRazonSocial("Proveedor Demo S.A. de C.V.");
                p.setEmailContacto("contacto@proveedor-demo.com");
                p.setTelefono("5555555555");
                p.setEstado(Proveedor.EstadoProveedor.ACTIVO);
                p.setTipoProveedor(Proveedor.TipoProveedor.EQUIPOS);
                proveedorRepository.save(p);
            }

            if (partidaRepository.count() == 0) {
                PartidaPresupuestaria partida = new PartidaPresupuestaria();
                partida.setCodigo("PDA-001");
                partida.setDescripcion("Adquisición de activos tecnológicos");
                partida.setAnioFiscal(Year.now().getValue());
                partida.setPresupuestoAsignado(new BigDecimal("500000.00"));
                partida.setPresupuestoEjecutado(BigDecimal.ZERO);
                partida.setPresupuestoDisponible(new BigDecimal("500000.00"));
                partida.setEstado(PartidaPresupuestaria.EstadoPartida.ACTIVA);
                partida.setCentroCosto("TI-001");
                partida.setResponsable("Gerencia TI");
                partidaRepository.save(partida);
            }
        };
    }

    private Usuario buildUser(String username, String nombre, Usuario.RolUsuario rol) {
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setNombreCompleto(nombre);
        user.setEmail(username + "@empresa.com");
        user.setDepartamento("General");
        user.setCentroCosto("CC-001");
        user.setRol(rol);
        return user;
    }
}
