package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.Adquisicion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdquisicionRepository extends JpaRepository<Adquisicion, Long> {
    boolean existsByNumeroFactura(String numeroFactura);
    Optional<Adquisicion> findByNumeroFactura(String numeroFactura);
    List<Adquisicion> findByFechaFacturaBetween(LocalDate inicio, LocalDate fin);
    Page<Adquisicion> findByProveedorId(Long proveedorId, Pageable pageable);
}
