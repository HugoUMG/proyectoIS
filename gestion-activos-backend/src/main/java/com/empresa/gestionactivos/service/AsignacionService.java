package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.AsignacionDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Activo;
import com.empresa.gestionactivos.model.Asignacion;
import com.empresa.gestionactivos.model.Usuario;
import com.empresa.gestionactivos.repository.ActivoRepository;
import com.empresa.gestionactivos.repository.AsignacionRepository;
import com.empresa.gestionactivos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignacionService {
    private final AsignacionRepository asignacionRepository;
    private final ActivoRepository activoRepository;
    private final UsuarioRepository usuarioRepository;

    public AsignacionDTO crearAsignacion(AsignacionDTO dto) {
        Activo activo = activoRepository.findById(dto.getActivoId()).orElseThrow(() -> new BusinessException("Activo no encontrado"));
        if (activo.getEstado() == Activo.EstadoActivo.DADO_DE_BAJA) {
            throw new BusinessException("No se puede asignar un activo dado de baja");
        }
        Usuario responsable = usuarioRepository.findById(dto.getUsuarioResponsableId())
            .orElseThrow(() -> new BusinessException("Usuario responsable no encontrado"));
        Usuario asignador = usuarioRepository.findById(dto.getAsignadoPorId())
            .orElseThrow(() -> new BusinessException("Usuario asignador no encontrado"));

        Asignacion entity = new Asignacion();
        entity.setActivo(activo);
        entity.setUsuarioResponsable(responsable);
        entity.setAsignadoPor(asignador);
        entity.setFechaAsignacion(dto.getFechaAsignacion() != null ? dto.getFechaAsignacion() : LocalDate.now());
        entity.setEstado(Asignacion.EstadoAsignacion.ACTIVA);
        entity.setObservaciones(dto.getObservaciones());
        entity.setDocumentoRespaldo(dto.getDocumentoRespaldo());

        activo.setEstado(Activo.EstadoActivo.ASIGNADO);
        activoRepository.save(activo);

        return toDto(asignacionRepository.save(entity));
    }

    public AsignacionDTO registrarDevolucion(Long id, String observaciones) {
        Asignacion asig = asignacionRepository.findById(id).orElseThrow(() -> new BusinessException("Asignación no encontrada"));
        asig.setFechaDevolucion(LocalDate.now());
        asig.setEstado(Asignacion.EstadoAsignacion.DEVUELTA);
        asig.setObservaciones(observaciones);

        Activo activo = asig.getActivo();
        if (activo != null && activo.getEstado() != Activo.EstadoActivo.DADO_DE_BAJA) {
            activo.setEstado(Activo.EstadoActivo.DISPONIBLE);
            activoRepository.save(activo);
        }
        return toDto(asignacionRepository.save(asig));
    }

    public List<AsignacionDTO> obtenerAsignacionesPorEmpleado(Long empleadoId) {
        return asignacionRepository.findByUsuarioResponsableId(empleadoId).stream().map(this::toDto).toList();
    }

    public List<AsignacionDTO> obtenerHistorialPorActivo(Long activoId) {
        return asignacionRepository.findByActivoIdOrderByFechaAsignacionDesc(activoId).stream().map(this::toDto).toList();
    }

    public void registrarFirma(Long id, String firmaBase64) {
        Asignacion asig = asignacionRepository.findById(id).orElseThrow(() -> new BusinessException("Asignación no encontrada"));
        asig.setFirmaDigital(firmaBase64);
        asignacionRepository.save(asig);
    }

    private AsignacionDTO toDto(Asignacion entity) {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(entity.getId());
        dto.setActivoId(entity.getActivo() != null ? entity.getActivo().getId() : null);
        dto.setUsuarioResponsableId(entity.getUsuarioResponsable() != null ? entity.getUsuarioResponsable().getId() : null);
        dto.setAsignadoPorId(entity.getAsignadoPor() != null ? entity.getAsignadoPor().getId() : null);
        dto.setFechaAsignacion(entity.getFechaAsignacion());
        dto.setFechaDevolucion(entity.getFechaDevolucion());
        dto.setEstado(entity.getEstado());
        dto.setObservaciones(entity.getObservaciones());
        dto.setDocumentoRespaldo(entity.getDocumentoRespaldo());
        dto.setFirmaDigital(entity.getFirmaDigital());
        return dto;
    }
}
