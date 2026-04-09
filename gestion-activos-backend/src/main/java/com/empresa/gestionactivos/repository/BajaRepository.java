package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.Baja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BajaRepository extends JpaRepository<Baja, Long> {
    boolean existsByActivoIdAndEstadoIn(Long activoId, List<Baja.EstadoBaja> estados);
    List<Baja> findByEstado(Baja.EstadoBaja estado);
    List<Baja> findByActivoId(Long activoId);
}
