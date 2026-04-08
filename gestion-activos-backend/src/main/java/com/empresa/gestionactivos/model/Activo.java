package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "activos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String codigoIdentificacion;  // Código QR/RFID
    
    @Column(nullable = false)
    private String nombre;
    
    private String descripcion;
    
    @Column(nullable = false)
    private String categoria;
    
    private String marca;
    private String modelo;
    private String numeroSerie;
    
    @Column(nullable = false)
    private BigDecimal valorCompra;
    
    private BigDecimal valorActual;
    
    @Column(nullable = false)
    private LocalDate fechaAdquisicion;
    
    private Integer vidaUtilMeses;
    
    @Enumerated(EnumType.STRING)
    private EstadoActivo estado;  // DISPONIBLE, ASIGNADO, EN_REPARACION, DADO_DE_BAJA
    
    private String ubicacionFisica;
    
    @ManyToOne
    @JoinColumn(name = "adquisicion_id")
    private Adquisicion adquisicion;
    
    @OneToMany(mappedBy = "activo")
    private List<Asignacion> asignaciones;
    
    @OneToOne(mappedBy = "activo")
    private Baja baja;
    
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion = LocalDate.now();
    
    @Column(name = "qr_code")
    @Lob
    private byte[] codigoQR;
    
    public enum EstadoActivo {
        DISPONIBLE, ASIGNADO, EN_REPARACION, DADO_DE_BAJA
    }
}