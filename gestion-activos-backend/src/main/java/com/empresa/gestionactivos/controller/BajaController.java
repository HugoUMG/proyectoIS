package com.empresa.gestionactivos.controller;

import com.empresa.gestionactivos.dto.BajaDTO;
import com.empresa.gestionactivos.dto.AprobacionBajaDTO;
import com.empresa.gestionactivos.service.BajaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bajas")
@RequiredArgsConstructor
@Tag(name = "Bajas y Enajenación", description = "Gestión de bajas de activos con flujo de aprobación")
@CrossOrigin(origins = "*")
public class BajaController {
    
    private final BajaService bajaService;
    
    @PostMapping("/solicitar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'EMPLEADO')")
    @Operation(summary = "Solicitar baja de un activo")
    public ResponseEntity<BajaDTO> solicitarBaja(@Valid @RequestBody BajaDTO dto) {
        return new ResponseEntity<>(bajaService.crearSolicitudBaja(dto), HttpStatus.CREATED);
    }
    
    @GetMapping("/pendientes-aprobacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Listar bajas pendientes de aprobación")
    public ResponseEntity<List<BajaDTO>> listarPendientesAprobacion() {
        return ResponseEntity.ok(bajaService.listarBajasPendientesAprobacion());
    }
    
    @GetMapping("/mis-aprobaciones-pendientes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar aprobaciones pendientes del usuario actual")
    public ResponseEntity<List<AprobacionBajaDTO>> misAprobacionesPendientes() {
        return ResponseEntity.ok(bajaService.obtenerAprobacionesPendientesUsuarioActual());
    }
    
    @PostMapping("/{bajaId}/aprobar/{nivel}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Aprobar solicitud de baja en un nivel específico")
    public ResponseEntity<BajaDTO> aprobarBaja(
            @PathVariable Long bajaId,
            @PathVariable Integer nivel,
            @RequestParam(required = false) String comentarios) {
        return ResponseEntity.ok(bajaService.aprobarBaja(bajaId, nivel, comentarios));
    }
    
    @PostMapping("/{bajaId}/rechazar/{nivel}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Rechazar solicitud de baja")
    public ResponseEntity<BajaDTO> rechazarBaja(
            @PathVariable Long bajaId,
            @PathVariable Integer nivel,
            @RequestParam String motivo) {
        return ResponseEntity.ok(bajaService.rechazarBaja(bajaId, nivel, motivo));
    }
    
    @PostMapping("/{bajaId}/ejecutar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO')")
    @Operation(summary = "Ejecutar baja aprobada")
    public ResponseEntity<BajaDTO> ejecutarBaja(
            @PathVariable Long bajaId,
            @RequestParam(required = false) BigDecimal valorVenta,
            @RequestParam(required = false) String comprobante) {
        return ResponseEntity.ok(bajaService.ejecutarBaja(bajaId, valorVenta, comprobante));
    }
    
    @GetMapping("/activo/{activoId}/historial")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar historial de bajas de un activo")
    public ResponseEntity<List<BajaDTO>> historialBajasActivo(@PathVariable Long activoId) {
        return ResponseEntity.ok(bajaService.obtenerHistorialBajasPorActivo(activoId));
    }
    
    @GetMapping("/{id}/flujo-aprobacion")
    @Operation(summary = "Consultar estado del flujo de aprobación")
    public ResponseEntity<List<AprobacionBajaDTO>> consultarFlujoAprobacion(@PathVariable Long id) {
        return ResponseEntity.ok(bajaService.consultarFlujoAprobacion(id));
    }
}