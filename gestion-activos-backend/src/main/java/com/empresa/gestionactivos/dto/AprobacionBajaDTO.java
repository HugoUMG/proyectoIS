package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.AprobacionBaja;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AprobacionBajaDTO {
    private Long id;
    private Long bajaId;
    private Integer nivelJerarquico;
    private Long aprobadorId;
    private String aprobadorNombre;
    private AprobacionBaja.DecisionAprobacion decision;
    private String comentarios;
    private LocalDate fechaAprobacion;
}
