package com.empresa.gestionactivos.controller;

import com.empresa.gestionactivos.dto.AsignacionDTO;
import com.empresa.gestionactivos.service.AsignacionService;
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
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
@Tag(name = "Asignaciones", description = "Gestión de préstamos y resguardos")
@CrossOrigin(origins = "*")
public class AsignacionController {
    
    private final AsignacionService asignacionService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO')")
    @Operation(summary = "Crear nueva asignación")
    public ResponseEntity<AsignacionDTO> crearAsignacion(@Valid @RequestBody AsignacionDTO dto) {
        return new ResponseEntity<>(asignacionService.crearAsignacion(dto), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}/devolver")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO')")
    @Operation(summary = "Registrar devolución de activo")
    public ResponseEntity<AsignacionDTO> registrarDevolucion(
            @PathVariable Long id,
            @RequestParam String observaciones) {
        return ResponseEntity.ok(asignacionService.registrarDevolucion(id, observaciones));
    }
    
    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener asignaciones por empleado")
    public ResponseEntity<List<AsignacionDTO>> obtenerPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(asignacionService.obtenerAsignacionesPorEmpleado(empleadoId));
    }
    
    @GetMapping("/activo/{activoId}/historial")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Historial de asignaciones por activo")
    public ResponseEntity<List<AsignacionDTO>> historialPorActivo(@PathVariable Long activoId) {
        return ResponseEntity.ok(asignacionService.obtenerHistorialPorActivo(activoId));
    }
    
    @PostMapping("/{id}/firma")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Registrar firma digital de recepción")
    public ResponseEntity<Void> registrarFirma(
            @PathVariable Long id,
            @RequestBody String firmaBase64) {
        asignacionService.registrarFirma(id, firmaBase64);
        return ResponseEntity.ok().build();
    }
}