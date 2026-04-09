package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.Asignacion;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AsignacionDTO {
    private Long id;
    private Long activoId;
    private Long usuarioResponsableId;
    private Long asignadoPorId;
    private LocalDate fechaAsignacion;
    private LocalDate fechaDevolucion;
    private Asignacion.EstadoAsignacion estado;
    private String observaciones;
    private String documentoRespaldo;
    private String firmaDigital;
}
