package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String rfc;
    
    @Column(nullable = false)
    private String razonSocial;
    
    private String nombreComercial;
    
    @Column(nullable = false)
    private String emailContacto;
    
    private String telefono;
    private String direccion;
    
    @Enumerated(EnumType.STRING)
    private TipoProveedor tipoProveedor;
    
    @Enumerated(EnumType.STRING)
    private EstadoProveedor estado;
    
    @OneToMany(mappedBy = "proveedor")
    private List<Adquisicion> adquisiciones;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro = LocalDate.now();
    
    private String condicionesPago;
    private Integer diasCredito;
    
    public enum TipoProveedor {
        NACIONAL, INTERNACIONAL, SERVICIOS, EQUIPOS, SOFTWARE
    }
    
    public enum EstadoProveedor {
        ACTIVO, INACTIVO, SUSPENDIDO
    }
}