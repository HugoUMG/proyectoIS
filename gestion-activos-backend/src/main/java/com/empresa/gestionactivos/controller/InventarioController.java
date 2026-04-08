package com.empresa.gestionactivos.controller;

import com.empresa.gestionactivos.dto.ActivoDTO;
import com.empresa.gestionactivos.service.InventarioService;
import com.empresa.gestionactivos.service.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de activos y bienes")
@CrossOrigin(origins = "*")
public class InventarioController {
    
    private final InventarioService inventarioService;
    private final QRCodeService qrCodeService;
    
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Listar todos los activos")
    public ResponseEntity<List<ActivoDTO>> listarActivos() {
        return ResponseEntity.ok(inventarioService.obtenerTodosActivos());
    }
    
    @GetMapping("/activos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Obtener activo por ID")
    public ResponseEntity<ActivoDTO> obtenerActivo(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerActivoPorId(id));
    }
    
    @PostMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO')")
    @Operation(summary = "Registrar nuevo activo")
    public ResponseEntity<ActivoDTO> crearActivo(@Valid @RequestBody ActivoDTO activoDTO) {
        ActivoDTO nuevoActivo = inventarioService.crearActivo(activoDTO);
        return new ResponseEntity<>(nuevoActivo, HttpStatus.CREATED);
    }
    
    @PutMapping("/activos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO')")
    @Operation(summary = "Actualizar activo")
    public ResponseEntity<ActivoDTO> actualizarActivo(
            @PathVariable Long id, 
            @Valid @RequestBody ActivoDTO activoDTO) {
        return ResponseEntity.ok(inventarioService.actualizarActivo(id, activoDTO));
    }
    
    @GetMapping("/activos/{id}/qr")
    @Operation(summary = "Generar código QR para activo")
    public ResponseEntity<byte[]> generarQR(@PathVariable Long id) {
        byte[] qrImage = qrCodeService.generateQRCodeImage(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrImage.length);
        
        return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
    }
    
    @GetMapping("/activos/buscar")
    @Operation(summary = "Buscar activos por criterios")
    public ResponseEntity<List<ActivoDTO>> buscarActivos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String ubicacion) {
        return ResponseEntity.ok(inventarioService.buscarActivos(categoria, estado, ubicacion));
    }
}