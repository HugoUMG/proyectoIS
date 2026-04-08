package com.empresa.gestionactivos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String nombreCompleto;
    
    private String email;
    private String departamento;
    private String centroCosto;
    
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;
    
    @OneToMany(mappedBy = "usuarioResponsable")
    private List<Asignacion> asignacionesActivas;
    
    public enum RolUsuario {
        ADMINISTRADOR,
        ENCARGADO_INVENTARIO,
        DEPARTAMENTO_COMPRAS,
        EMPLEADO,
        GERENCIA_FINANZAS
    }
}