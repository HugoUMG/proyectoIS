package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.AdquisicionDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Adquisicion;
import com.empresa.gestionactivos.model.PartidaPresupuestaria;
import com.empresa.gestionactivos.repository.AdquisicionRepository;
import com.empresa.gestionactivos.repository.PartidaPresupuestariaRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdquisicionService {
    
    private final AdquisicionRepository adquisicionRepository;
    private final PartidaPresupuestariaRepository partidaRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final String UPLOAD_DIR = "uploads/facturas/";
    
    public AdquisicionDTO crearAdquisicion(AdquisicionDTO dto) {
        // Validar que la partida presupuestaria exista y tenga saldo
        PartidaPresupuestaria partida = partidaRepository.findByCodigo(dto.getPartidaPresupuestaria())
            .orElseThrow(() -> new BusinessException("Partida presupuestaria no encontrada"));
        
        if (partida.getPresupuestoDisponible().compareTo(dto.getTotal()) < 0) {
            throw new BusinessException("Saldo insuficiente en la partida presupuestaria. Disponible: " + 
                partida.getPresupuestoDisponible());
        }
        
        // Validar que la factura no esté duplicada
        if (adquisicionRepository.existsByNumeroFactura(dto.getNumeroFactura())) {
            throw new BusinessException("Ya existe una adquisición con este número de factura");
        }
        
        Adquisicion adquisicion = mapToEntity(dto);
        adquisicion.setEstado(Adquisicion.EstadoAdquisicion.PENDIENTE);
        
        // Actualizar saldo de la partida
        partida.setPresupuestoEjecutado(partida.getPresupuestoEjecutado().add(dto.getTotal()));
        partida.actualizarDisponible();
        partidaRepository.save(partida);
        
        Adquisicion saved = adquisicionRepository.save(adquisicion);
        log.info("Adquisición registrada: Factura {} por ${}", dto.getNumeroFactura(), dto.getTotal());
        
        return mapToDTO(saved);
    }
    
    public void adjuntarFactura(Long id, MultipartFile archivo) {
        try {
            Adquisicion adquisicion = adquisicionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Adquisición no encontrada"));
            
            // Crear directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generar nombre único
            String extension = getFileExtension(archivo.getOriginalFilename());
            String nombreArchivo = UUID.randomUUID().toString() + "_" + 
                adquisicion.getNumeroFactura().replace("/", "_") + extension;
            
            // Guardar archivo
            Path filePath = uploadPath.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), filePath);
            
            // Actualizar referencia
            adquisicion.setArchivoFactura(filePath.toString());
            adquisicionRepository.save(adquisicion);
            
            log.info("Factura adjuntada para adquisición ID: {}", id);
        } catch (Exception e) {
            log.error("Error al adjuntar factura", e);
            throw new BusinessException("Error al guardar el archivo de factura: " + e.getMessage());
        }
    }
    
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
    
    public AdquisicionDTO cambiarEstado(Long id, String estado, String comentarios) {
        Adquisicion adquisicion = adquisicionRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Adquisición no encontrada"));
        
        try {
            Adquisicion.EstadoAdquisicion nuevoEstado = Adquisicion.EstadoAdquisicion.valueOf(estado.toUpperCase());
            adquisicion.setEstado(nuevoEstado);
            
            if (comentarios != null) {
                adquisicion.setObservaciones(comentarios);
            }
            
            Adquisicion saved = adquisicionRepository.save(adquisicion);
            log.info("Estado de adquisición {} cambiado a {}", id, estado);
            
            return mapToDTO(saved);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado no válido: " + estado);
        }
    }
}