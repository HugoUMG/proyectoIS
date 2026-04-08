package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "adquisiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adquisicion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String numeroFactura;
    
    @Column(nullable = false)
    private LocalDate fechaFactura;
    
    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    private BigDecimal iva;
    
    @Column(nullable = false)
    private BigDecimal total;
    
    @Column(name = "partida_presupuestaria", nullable = false)
    private String partidaPresupuestaria;
    
    @Enumerated(EnumType.STRING)
    private EstadoAdquisicion estado;
    
    private String observaciones;
    
    @Column(name = "archivo_factura")
    private String archivoFactura; // Ruta al PDF/XML
    
    @OneToMany(mappedBy = "adquisicion", cascade = CascadeType.ALL)
    private List<Activo> activos;
    
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro = LocalDate.now();
    
    @Column(name = "numero_orden_compra")
    private String numeroOrdenCompra;
    
    @Column(name = "fecha_orden_compra")
    private LocalDate fechaOrdenCompra;
    
    public enum EstadoAdquisicion {
        PENDIENTE, APROBADA, RECHAZADA, COMPLETADA, CANCELADA
    }
}