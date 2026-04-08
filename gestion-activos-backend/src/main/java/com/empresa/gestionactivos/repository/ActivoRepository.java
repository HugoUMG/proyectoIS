// ActivoRepository.java
package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.Activo;
import com.empresa.gestionactivos.model.Activo.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivoRepository extends JpaRepository<Activo, Long> {
    Optional<Activo> findByCodigoIdentificacion(String codigo);
    List<Activo> findByEstado(EstadoActivo estado);
    List<Activo> findByCategoria(String categoria);
    
    @Query("SELECT a FROM Activo a WHERE a.estado = 'ASIGNADO'")
    List<Activo> findActivosAsignados();
    
    @Query("SELECT a FROM Activo a WHERE a.vidaUtilMeses IS NOT NULL " +
           "AND a.fechaAdquisicion IS NOT NULL")
    List<Activo> findActivosDepreciables();
    
    @Query(value = "SELECT * FROM activos WHERE " +
           "DATE_ADD(fecha_adquisicion, INTERVAL vida_util_meses MONTH) " +
           "BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 MONTH)", 
           nativeQuery = true)
    List<Activo> findActivosProximosABaja();
}