package com.empresa.gestionactivos.dto;

import com.empresa.gestionactivos.model.Proveedor;
import lombok.Data;

@Data
public class ProveedorDTO {
    private Long id;
    private String rfc;
    private String razonSocial;
    private String nombreComercial;
    private String emailContacto;
    private String telefono;
    private String direccion;
    private Proveedor.TipoProveedor tipoProveedor;
    private Proveedor.EstadoProveedor estado;
}
