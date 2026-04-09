package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.ProveedorDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Proveedor;
import com.empresa.gestionactivos.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;

    public List<ProveedorDTO> listarProveedoresActivos() {
        return proveedorRepository.findByEstado(Proveedor.EstadoProveedor.ACTIVO).stream().map(this::toDto).toList();
    }

    public ProveedorDTO crearProveedor(ProveedorDTO dto) {
        Proveedor entity = toEntity(dto);
        if (entity.getEstado() == null) {
            entity.setEstado(Proveedor.EstadoProveedor.ACTIVO);
        }
        return toDto(proveedorRepository.save(entity));
    }

    public ProveedorDTO obtenerProveedor(Long id) {
        return toDto(proveedorRepository.findById(id).orElseThrow(() -> new BusinessException("Proveedor no encontrado")));
    }

    public ProveedorDTO actualizarProveedor(Long id, ProveedorDTO dto) {
        Proveedor existing = proveedorRepository.findById(id).orElseThrow(() -> new BusinessException("Proveedor no encontrado"));
        existing.setRazonSocial(dto.getRazonSocial());
        existing.setEmailContacto(dto.getEmailContacto());
        existing.setDireccion(dto.getDireccion());
        existing.setTelefono(dto.getTelefono());
        existing.setNombreComercial(dto.getNombreComercial());
        existing.setTipoProveedor(dto.getTipoProveedor());
        existing.setEstado(dto.getEstado());
        return toDto(proveedorRepository.save(existing));
    }

    public List<ProveedorDTO> buscarProveedores(String termino) {
        return proveedorRepository.findByRfcContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(termino, termino)
            .stream().map(this::toDto).toList();
    }

    private ProveedorDTO toDto(Proveedor p) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(p.getId());
        dto.setRfc(p.getRfc());
        dto.setRazonSocial(p.getRazonSocial());
        dto.setNombreComercial(p.getNombreComercial());
        dto.setEmailContacto(p.getEmailContacto());
        dto.setTelefono(p.getTelefono());
        dto.setDireccion(p.getDireccion());
        dto.setTipoProveedor(p.getTipoProveedor());
        dto.setEstado(p.getEstado());
        return dto;
    }

    private Proveedor toEntity(ProveedorDTO dto) {
        Proveedor p = new Proveedor();
        p.setId(dto.getId());
        p.setRfc(dto.getRfc());
        p.setRazonSocial(dto.getRazonSocial());
        p.setNombreComercial(dto.getNombreComercial());
        p.setEmailContacto(dto.getEmailContacto());
        p.setTelefono(dto.getTelefono());
        p.setDireccion(dto.getDireccion());
        p.setTipoProveedor(dto.getTipoProveedor());
        p.setEstado(dto.getEstado());
        return p;
    }
}
