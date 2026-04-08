package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bajas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Baja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "activo_id", nullable = false, unique = true)
    private Activo activo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBaja tipoBaja;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoBaja motivoBaja;
    
    @Column(nullable = false, length = 500)
    private String justificacion;
    
    @Column(name = "valor_residual")
    private BigDecimal valorResidual;
    
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud = LocalDate.now();
    
    @ManyToOne
    @JoinColumn(name = "solicitado_por_id", nullable = false)
    private Usuario solicitadoPor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBaja estado;
    
    @Column(name = "fecha_ejecucion")
    private LocalDate fechaEjecucion;
    
    @ManyToOne
    @JoinColumn(name = "ejecutado_por_id")
    private Usuario ejecutadoPor;
    
    @Column(name = "valor_venta")
    private BigDecimal valorVenta;
    
    @Column(name = "comprobante_venta")
    private String comprobanteVenta;
    
    @OneToMany(mappedBy = "baja", cascade = CascadeType.ALL)
    private List<AprobacionBaja> aprobaciones;
    
    private String observaciones;
    
    public enum TipoBaja {
        OBSOLESCENCIA, DAÑO_IRREPARABLE, VENTA, DONACION, ROBO, PERDIDA
    }
    
    public enum MotivoBaja {
        FIN_VIDA_UTIL, FALLA_TECNICA, ACTUALIZACION_TECNOLOGICA, 
        DAÑO_FISICO, ROBO_COMPROBADO, VENTA_ACTIVO, DONACION_INSTITUCIONAL
    }
    
    public enum EstadoBaja {
        BORRADOR, PENDIENTE_APROBACION, APROBADA, RECHAZADA, EJECUTADA, CANCELADA
    }
}