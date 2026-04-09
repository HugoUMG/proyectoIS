package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.AdquisicionDTO;
import com.empresa.gestionactivos.dto.PartidaPresupuestariaDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Adquisicion;
import com.empresa.gestionactivos.model.PartidaPresupuestaria;
import com.empresa.gestionactivos.repository.AdquisicionRepository;
import com.empresa.gestionactivos.repository.PartidaPresupuestariaRepository;
import com.empresa.gestionactivos.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdquisicionService {

    private final AdquisicionRepository adquisicionRepository;
    private final PartidaPresupuestariaRepository partidaRepository;
    private final ProveedorRepository proveedorRepository;
    private final String uploadDir = "uploads/facturas";

    public AdquisicionDTO crearAdquisicion(AdquisicionDTO dto) {
        PartidaPresupuestaria partida = partidaRepository.findByCodigo(dto.getPartidaPresupuestaria())
            .orElseThrow(() -> new BusinessException("Partida presupuestaria no encontrada"));
        if (partida.getPresupuestoDisponible() != null && partida.getPresupuestoDisponible().compareTo(dto.getTotal()) < 0) {
            throw new BusinessException("Saldo insuficiente en partida presupuestaria");
        }
        if (adquisicionRepository.existsByNumeroFactura(dto.getNumeroFactura())) {
            throw new BusinessException("La factura ya está registrada");
        }

        Adquisicion entity = new Adquisicion();
        entity.setNumeroFactura(dto.getNumeroFactura());
        entity.setFechaFactura(dto.getFechaFactura() != null ? dto.getFechaFactura() : LocalDate.now());
        entity.setProveedor(proveedorRepository.findById(dto.getProveedorId()).orElseThrow(() -> new BusinessException("Proveedor no encontrado")));
        entity.setSubtotal(dto.getSubtotal());
        entity.setIva(dto.getIva());
        entity.setTotal(dto.getTotal());
        entity.setPartidaPresupuestaria(dto.getPartidaPresupuestaria());
        entity.setEstado(Adquisicion.EstadoAdquisicion.PENDIENTE);
        entity.setObservaciones(dto.getObservaciones());

        partida.setPresupuestoEjecutado((partida.getPresupuestoEjecutado() == null ? BigDecimal.ZERO : partida.getPresupuestoEjecutado()).add(dto.getTotal()));
        if (partida.getPresupuestoAsignado() != null) {
            partida.actualizarDisponible();
        }
        partidaRepository.save(partida);

        return toDto(adquisicionRepository.save(entity));
    }

    public void adjuntarFactura(Long id, MultipartFile archivo) {
        try {
            Adquisicion adquisicion = adquisicionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Adquisición no encontrada"));
            Path path = Paths.get(uploadDir);
            Files.createDirectories(path);
            String filename = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path filePath = path.resolve(filename);
            Files.copy(archivo.getInputStream(), filePath);
            adquisicion.setArchivoFactura(filePath.toString());
            adquisicionRepository.save(adquisicion);
        } catch (Exception e) {
            throw new BusinessException("No se pudo adjuntar la factura: " + e.getMessage());
        }
    }

    public Page<AdquisicionDTO> listarAdquisiciones(Pageable pageable, LocalDate fechaInicio, LocalDate fechaFin, Long proveedorId) {
        return adquisicionRepository.findAll(pageable).map(this::toDto);
    }

    public AdquisicionDTO obtenerAdquisicion(Long id) {
        return toDto(adquisicionRepository.findById(id).orElseThrow(() -> new BusinessException("Adquisición no encontrada")));
    }

    public AdquisicionDTO buscarPorNumeroFactura(String numeroFactura) {
        return toDto(adquisicionRepository.findByNumeroFactura(numeroFactura).orElseThrow(() -> new BusinessException("Adquisición no encontrada")));
    }

    public AdquisicionDTO cambiarEstado(Long id, String estado, String comentarios) {
        Adquisicion adq = adquisicionRepository.findById(id).orElseThrow(() -> new BusinessException("Adquisición no encontrada"));
        adq.setEstado(Adquisicion.EstadoAdquisicion.valueOf(estado.toUpperCase()));
        adq.setObservaciones(comentarios);
        return toDto(adquisicionRepository.save(adq));
    }

    public List<PartidaPresupuestariaDTO> listarPartidasActivas() {
        return partidaRepository.findByEstado(PartidaPresupuestaria.EstadoPartida.ACTIVA).stream().map(p -> {
            PartidaPresupuestariaDTO dto = new PartidaPresupuestariaDTO();
            dto.setId(p.getId());
            dto.setCodigo(p.getCodigo());
            dto.setDescripcion(p.getDescripcion());
            dto.setAnioFiscal(p.getAnioFiscal());
            dto.setPresupuestoAsignado(p.getPresupuestoAsignado());
            dto.setPresupuestoEjecutado(p.getPresupuestoEjecutado());
            dto.setPresupuestoDisponible(p.getPresupuestoDisponible());
            dto.setEstado(p.getEstado());
            dto.setCentroCosto(p.getCentroCosto());
            dto.setResponsable(p.getResponsable());
            return dto;
        }).toList();
    }

    public BigDecimal consultarSaldoDisponible(String codigo) {
        return partidaRepository.findByCodigo(codigo).orElseThrow(() -> new BusinessException("Partida no encontrada")).getPresupuestoDisponible();
    }

    public Boolean validarDisponibilidadPartida(String codigoPartida, BigDecimal monto) {
        BigDecimal disponible = consultarSaldoDisponible(codigoPartida);
        return disponible != null && disponible.compareTo(monto) >= 0;
    }

    private AdquisicionDTO toDto(Adquisicion adq) {
        AdquisicionDTO dto = new AdquisicionDTO();
        dto.setId(adq.getId());
        dto.setNumeroFactura(adq.getNumeroFactura());
        dto.setFechaFactura(adq.getFechaFactura());
        dto.setProveedorId(adq.getProveedor() != null ? adq.getProveedor().getId() : null);
        dto.setProveedorNombre(adq.getProveedor() != null ? adq.getProveedor().getRazonSocial() : null);
        dto.setSubtotal(adq.getSubtotal());
        dto.setIva(adq.getIva());
        dto.setTotal(adq.getTotal());
        dto.setPartidaPresupuestaria(adq.getPartidaPresupuestaria());
        dto.setEstado(adq.getEstado());
        dto.setObservaciones(adq.getObservaciones());
        dto.setArchivoFactura(adq.getArchivoFactura());
        return dto;
    }
}
