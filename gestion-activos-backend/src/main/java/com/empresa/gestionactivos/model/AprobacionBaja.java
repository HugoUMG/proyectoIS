package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "aprobaciones_baja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AprobacionBaja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "baja_id", nullable = false)
    private Baja baja;
    
    @ManyToOne
    @JoinColumn(name = "aprobador_id", nullable = false)
    private Usuario aprobador;
    
    @Column(nullable = false)
    private Integer nivelJerarquico; // 1: Jefe Inmediato, 2: Gerente, 3: Finanzas
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionAprobacion decision;
    
    @Column(length = 500)
    private String comentarios;
    
    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion = LocalDate.now();
    
    public enum DecisionAprobacion {
        PENDIENTE, APROBADO, RECHAZADO
    }
}