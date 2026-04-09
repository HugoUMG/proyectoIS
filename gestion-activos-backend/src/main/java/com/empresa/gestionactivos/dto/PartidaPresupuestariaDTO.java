package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.PartidaPresupuestaria;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartidaPresupuestariaDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private Integer anioFiscal;
    private BigDecimal presupuestoAsignado;
    private BigDecimal presupuestoEjecutado;
    private BigDecimal presupuestoDisponible;
    private PartidaPresupuestaria.EstadoPartida estado;
    private String centroCosto;
    private String responsable;
}
