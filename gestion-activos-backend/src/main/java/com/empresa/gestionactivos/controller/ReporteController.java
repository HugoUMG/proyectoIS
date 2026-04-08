package com.empresa.gestionactivos.controller;

import com.empresa.gestionactivos.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación de reportes en PDF y Excel")
@CrossOrigin(origins = "*")
public class ReporteController {
    
    private final ReporteService reporteService;
    
    @GetMapping("/empleado/{empleadoId}/hoja-vida")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Generar hoja de vida de activos por empleado (PDF)")
    public ResponseEntity<byte[]> generarHojaVidaEmpleado(@PathVariable Long empleadoId) {
        byte[] pdfBytes = reporteService.generarHojaVidaEmpleado(empleadoId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "hoja-vida-empleado.pdf");
        
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
    
    @GetMapping("/centro-costo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte por centro de costo (Excel)")
    public ResponseEntity<byte[]> reportePorCentroCosto(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        byte[] excelBytes = reporteService.generarReporteCentroCosto(fechaInicio, fechaFin);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "reporte-centro-costo.xlsx");
        
        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
    
    @GetMapping("/depreciacion")
    @PreAuthorize("hasRole('GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte de depreciación acumulada (Excel)")
    public ResponseEntity<byte[]> reporteDepreciacion() {
        byte[] excelBytes = reporteService.generarReporteDepreciacion();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "reporte-depreciacion.xlsx");
        
        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
    
    @GetMapping("/proximos-baja")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte de bienes próximos a baja (PDF/Excel)")
    public ResponseEntity<byte[]> reporteProximosBaja(
            @RequestParam(defaultValue = "PDF") String formato) {
        
        byte[] reporteBytes = reporteService.generarReporteProximosBaja(formato);
        
        HttpHeaders headers = new HttpHeaders();
        if ("PDF".equalsIgnoreCase(formato)) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "proximos-baja.pdf");
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "proximos-baja.xlsx");
        }
        
        return ResponseEntity.ok().headers(headers).body(reporteBytes);
    }
}