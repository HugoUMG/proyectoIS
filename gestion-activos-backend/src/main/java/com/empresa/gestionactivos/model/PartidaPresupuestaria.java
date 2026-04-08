package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "partidas_presupuestarias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartidaPresupuestaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String codigo;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Integer anioFiscal;
    
    private BigDecimal presupuestoAsignado;
    private BigDecimal presupuestoEjecutado;
    private BigDecimal presupuestoDisponible;
    
    @Enumerated(EnumType.STRING)
    private EstadoPartida estado;
    
    private String centroCosto;
    private String responsable;
    
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion = LocalDate.now();
    
    public enum EstadoPartida {
        ACTIVA, CONGELADA, CERRADA
    }
    
    public void actualizarDisponible() {
        this.presupuestoDisponible = this.presupuestoAsignado.subtract(this.presupuestoEjecutado);
    }
}