package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.Activo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ActivoDTO {
    private Long id;
    private String codigoIdentificacion;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private BigDecimal valorCompra;
    private BigDecimal valorActual;
    private LocalDate fechaAdquisicion;
    private Integer vidaUtilMeses;
    private Activo.EstadoActivo estado;
    private String ubicacionFisica;
    private Long adquisicionId;
}
