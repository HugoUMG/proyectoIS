package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByUsuarioResponsableId(Long usuarioId);
    List<Asignacion> findByActivoIdOrderByFechaAsignacionDesc(Long activoId);
    List<Asignacion> findByEstado(Asignacion.EstadoAsignacion estado);
}
