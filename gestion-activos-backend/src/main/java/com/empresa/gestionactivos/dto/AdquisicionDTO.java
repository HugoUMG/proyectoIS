package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.Adquisicion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AdquisicionDTO {
    private Long id;
    private String numeroFactura;
    private LocalDate fechaFactura;
    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String partidaPresupuestaria;
    private Adquisicion.EstadoAdquisicion estado;
    private String observaciones;
    private String archivoFactura;
}
