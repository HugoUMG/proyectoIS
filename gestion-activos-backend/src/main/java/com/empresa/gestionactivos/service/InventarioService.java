package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.ActivoDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Activo;
import com.empresa.gestionactivos.repository.ActivoRepository;
import com.empresa.gestionactivos.repository.AdquisicionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final ActivoRepository activoRepository;
    private final AdquisicionRepository adquisicionRepository;

    public List<ActivoDTO> obtenerTodosActivos() {
        return activoRepository.findAll().stream().map(this::toDto).toList();
    }

    public ActivoDTO obtenerActivoPorId(Long id) {
        return toDto(activoRepository.findById(id).orElseThrow(() -> new BusinessException("Activo no encontrado")));
    }

    public ActivoDTO crearActivo(ActivoDTO dto) {
        Activo activo = toEntity(dto);
        if (activo.getCodigoIdentificacion() == null || activo.getCodigoIdentificacion().isBlank()) {
            activo.setCodigoIdentificacion("ACT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (activo.getEstado() == null) {
            activo.setEstado(Activo.EstadoActivo.DISPONIBLE);
        }
        if (activo.getFechaAdquisicion() == null) {
            activo.setFechaAdquisicion(LocalDate.now());
        }
        return toDto(activoRepository.save(activo));
    }

    public ActivoDTO actualizarActivo(Long id, ActivoDTO dto) {
        Activo existing = activoRepository.findById(id).orElseThrow(() -> new BusinessException("Activo no encontrado"));
        existing.setNombre(dto.getNombre());
        existing.setDescripcion(dto.getDescripcion());
        existing.setCategoria(dto.getCategoria());
        existing.setMarca(dto.getMarca());
        existing.setModelo(dto.getModelo());
        existing.setNumeroSerie(dto.getNumeroSerie());
        existing.setValorCompra(dto.getValorCompra());
        existing.setValorActual(dto.getValorActual());
        existing.setEstado(dto.getEstado());
        existing.setUbicacionFisica(dto.getUbicacionFisica());
        existing.setVidaUtilMeses(dto.getVidaUtilMeses());
        return toDto(activoRepository.save(existing));
    }

    public List<ActivoDTO> buscarActivos(String categoria, String estado, String ubicacion) {
        return activoRepository.findAll().stream().filter(a ->
                (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase(a.getCategoria())) &&
                (estado == null || estado.isBlank() || a.getEstado().name().equalsIgnoreCase(estado)) &&
                (ubicacion == null || ubicacion.isBlank() ||
                    (a.getUbicacionFisica() != null && a.getUbicacionFisica().toLowerCase().contains(ubicacion.toLowerCase())))
            ).map(this::toDto).toList();
    }

    private ActivoDTO toDto(Activo activo) {
        ActivoDTO dto = new ActivoDTO();
        dto.setId(activo.getId());
        dto.setCodigoIdentificacion(activo.getCodigoIdentificacion());
        dto.setNombre(activo.getNombre());
        dto.setDescripcion(activo.getDescripcion());
        dto.setCategoria(activo.getCategoria());
        dto.setMarca(activo.getMarca());
        dto.setModelo(activo.getModelo());
        dto.setNumeroSerie(activo.getNumeroSerie());
        dto.setValorCompra(activo.getValorCompra());
        dto.setValorActual(activo.getValorActual());
        dto.setFechaAdquisicion(activo.getFechaAdquisicion());
        dto.setVidaUtilMeses(activo.getVidaUtilMeses());
        dto.setEstado(activo.getEstado());
        dto.setUbicacionFisica(activo.getUbicacionFisica());
        dto.setAdquisicionId(activo.getAdquisicion() != null ? activo.getAdquisicion().getId() : null);
        return dto;
    }

    private Activo toEntity(ActivoDTO dto) {
        Activo activo = new Activo();
        activo.setId(dto.getId());
        activo.setCodigoIdentificacion(dto.getCodigoIdentificacion());
        activo.setNombre(dto.getNombre());
        activo.setDescripcion(dto.getDescripcion());
        activo.setCategoria(dto.getCategoria());
        activo.setMarca(dto.getMarca());
        activo.setModelo(dto.getModelo());
        activo.setNumeroSerie(dto.getNumeroSerie());
        activo.setValorCompra(dto.getValorCompra());
        activo.setValorActual(dto.getValorActual());
        activo.setFechaAdquisicion(dto.getFechaAdquisicion());
        activo.setVidaUtilMeses(dto.getVidaUtilMeses());
        activo.setEstado(dto.getEstado());
        activo.setUbicacionFisica(dto.getUbicacionFisica());
        if (dto.getAdquisicionId() != null) {
            activo.setAdquisicion(adquisicionRepository.findById(dto.getAdquisicionId()).orElse(null));
        }
        return activo;
    }
}
