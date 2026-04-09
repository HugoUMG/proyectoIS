package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.PartidaPresupuestaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartidaPresupuestariaRepository extends JpaRepository<PartidaPresupuestaria, Long> {
    Optional<PartidaPresupuestaria> findByCodigo(String codigo);
    List<PartidaPresupuestaria> findByEstado(PartidaPresupuestaria.EstadoPartida estado);
}
