package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.Baja;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BajaDTO {
    private Long id;
    private Long activoId;
    private Baja.TipoBaja tipoBaja;
    private Baja.MotivoBaja motivoBaja;
    private String justificacion;
    private BigDecimal valorResidual;
    private LocalDate fechaSolicitud;
    private Long solicitadoPorId;
    private Baja.EstadoBaja estado;
    private LocalDate fechaEjecucion;
    private Long ejecutadoPorId;
    private BigDecimal valorVenta;
    private String comprobanteVenta;
    private String observaciones;
}
