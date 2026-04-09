package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.AprobacionBaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AprobacionBajaRepository extends JpaRepository<AprobacionBaja, Long> {
    Optional<AprobacionBaja> findByBajaIdAndNivelJerarquico(Long bajaId, Integer nivel);
    List<AprobacionBaja> findByBajaId(Long bajaId);
    List<AprobacionBaja> findByAprobadorUsernameAndDecision(String username, AprobacionBaja.DecisionAprobacion decision);
}
