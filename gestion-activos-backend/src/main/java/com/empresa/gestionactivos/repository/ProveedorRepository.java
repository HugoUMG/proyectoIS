package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByEstado(Proveedor.EstadoProveedor estado);
    List<Proveedor> findByRfcContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(String rfc, String razonSocial);
}
