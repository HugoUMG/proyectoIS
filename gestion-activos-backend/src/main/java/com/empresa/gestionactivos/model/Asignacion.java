package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuarioResponsable;
    
    @ManyToOne
    @JoinColumn(name = "asignado_por_id", nullable = false)
    private Usuario asignadoPor;
    
    @Column(nullable = false)
    private LocalDate fechaAsignacion;
    
    private LocalDate fechaDevolucion;
    
    @Enumerated(EnumType.STRING)
    private EstadoAsignacion estado;
    
    private String observaciones;
    
    @Column(name = "documento_responsabilidad")
    private String documentoRespaldo;  // Ruta al PDF de resguardo
    
    @Column(name = "firma_digital")
    @Lob
    private String firmaDigital;  // Base64 de la firma
    
    public enum EstadoAsignacion {
        ACTIVA, DEVUELTA, TRANSFERIDA
    }
}