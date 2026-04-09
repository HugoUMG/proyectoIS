package com.empresa.gestionactivos.controller;

import com.empresa.gestionactivos.dto.AdquisicionDTO;
import com.empresa.gestionactivos.dto.ProveedorDTO;
import com.empresa.gestionactivos.dto.PartidaPresupuestariaDTO;
import com.empresa.gestionactivos.service.AdquisicionService;
import com.empresa.gestionactivos.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/adquisiciones")
@RequiredArgsConstructor
@Tag(name = "Adquisiciones", description = "Gestión de compras y facturas")
@CrossOrigin(origins = "*")
public class AdquisicionController {
    
    private final AdquisicionService adquisicionService;
    private final ProveedorService proveedorService;
    
    // ============ ADQUISICIONES ============
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS')")
    @Operation(summary = "Registrar nueva adquisición desde factura")
    public ResponseEntity<AdquisicionDTO> registrarAdquisicion(@Valid @RequestBody AdquisicionDTO dto) {
        return new ResponseEntity<>(adquisicionService.crearAdquisicion(dto), HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/{id}/factura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS')")
    @Operation(summary = "Subir archivo de factura (PDF/XML)")
    public ResponseEntity<Void> subirFactura(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) {
        adquisicionService.adjuntarFactura(id, archivo);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Listar todas las adquisiciones")
    public ResponseEntity<Page<AdquisicionDTO>> listarAdquisiciones(
            Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long proveedorId) {
        return ResponseEntity.ok(adquisicionService.listarAdquisiciones(pageable, fechaInicio, fechaFin, proveedorId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Obtener adquisición por ID")
    public ResponseEntity<AdquisicionDTO> obtenerAdquisicion(@PathVariable Long id) {
        return ResponseEntity.ok(adquisicionService.obtenerAdquisicion(id));
    }
    
    @GetMapping("/factura/{numeroFactura}")
    @Operation(summary = "Buscar adquisición por número de factura")
    public ResponseEntity<AdquisicionDTO> buscarPorFactura(@PathVariable String numeroFactura) {
        return ResponseEntity.ok(adquisicionService.buscarPorNumeroFactura(numeroFactura));
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Aprobar o rechazar adquisición")
    public ResponseEntity<AdquisicionDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String comentarios) {
        return ResponseEntity.ok(adquisicionService.cambiarEstado(id, estado, comentarios));
    }
    
    // ============ PROVEEDORES ============
    
    @GetMapping("/proveedores")
    @Operation(summary = "Listar catálogo de proveedores")
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        return ResponseEntity.ok(proveedorService.listarProveedoresActivos());
    }
    
    @PostMapping("/proveedores")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS')")
    @Operation(summary = "Registrar nuevo proveedor")
    public ResponseEntity<ProveedorDTO> registrarProveedor(@Valid @RequestBody ProveedorDTO dto) {
        return new ResponseEntity<>(proveedorService.crearProveedor(dto), HttpStatus.CREATED);
    }
    
    @GetMapping("/proveedores/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    public ResponseEntity<ProveedorDTO> obtenerProveedor(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerProveedor(id));
    }
    
    @PutMapping("/proveedores/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS')")
    @Operation(summary = "Actualizar información de proveedor")
    public ResponseEntity<ProveedorDTO> actualizarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(proveedorService.actualizarProveedor(id, dto));
    }
    
    @GetMapping("/proveedores/buscar")
    @Operation(summary = "Buscar proveedores por RFC o razón social")
    public ResponseEntity<List<ProveedorDTO>> buscarProveedores(@RequestParam String termino) {
        return ResponseEntity.ok(proveedorService.buscarProveedores(termino));
    }
    
    // ============ PARTIDAS PRESUPUESTARIAS ============
    
    @GetMapping("/partidas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DEPARTAMENTO_COMPRAS', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Listar partidas presupuestarias activas")
    public ResponseEntity<List<PartidaPresupuestariaDTO>> listarPartidasActivas() {
        return ResponseEntity.ok(adquisicionService.listarPartidasActivas());
    }
    
    @GetMapping("/partidas/{codigo}/disponible")
    @Operation(summary = "Consultar saldo disponible de partida")
    public ResponseEntity<BigDecimal> consultarSaldoDisponible(@PathVariable String codigo) {
        return ResponseEntity.ok(adquisicionService.consultarSaldoDisponible(codigo));
    }
    
    @GetMapping("/partidas/validar")
    @Operation(summary = "Validar si una partida tiene saldo suficiente")
    public ResponseEntity<Boolean> validarPartida(
            @RequestParam String codigoPartida,
            @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(adquisicionService.validarDisponibilidadPartida(codigoPartida, monto));
    }
}