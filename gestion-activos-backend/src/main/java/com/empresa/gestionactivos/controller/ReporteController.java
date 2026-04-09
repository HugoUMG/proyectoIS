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
    @Operation(summary = "Generar hoja de vida de activos por empleado")
    public ResponseEntity<byte[]> generarHojaVidaEmpleado(@PathVariable Long empleadoId) {
        return asAttachment(reporteService.generarHojaVidaEmpleado(empleadoId), "hoja-vida-empleado.csv", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/centro-costo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte por centro de costo")
    public ResponseEntity<byte[]> reportePorCentroCosto(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return asAttachment(reporteService.generarReporteCentroCosto(fechaInicio, fechaFin), "reporte-centro-costo.csv", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/depreciacion")
    @PreAuthorize("hasRole('GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte de depreciación acumulada")
    public ResponseEntity<byte[]> reporteDepreciacion() {
        return asAttachment(reporteService.generarReporteDepreciacion(), "reporte-depreciacion.csv", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/proximos-baja")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte de bienes próximos a baja")
    public ResponseEntity<byte[]> reporteProximosBaja(@RequestParam(defaultValue = "CSV") String formato) {
        return asAttachment(reporteService.generarReporteProximosBaja(formato), "proximos-baja.csv", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/bienes-invertidos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte de bienes invertidos en la empresa")
    public ResponseEntity<byte[]> reporteBienesInvertidos(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return asAttachment(reporteService.generarReporteBienesInvertidos(fechaInicio, fechaFin), "bienes-invertidos.csv", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/bienes-asignados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_INVENTARIO', 'GERENCIA_FINANZAS')")
    @Operation(summary = "Reporte de bienes asignados a empleados")
    public ResponseEntity<byte[]> reporteBienesAsignados(@RequestParam(defaultValue = "CSV") String formato) {
        return asAttachment(reporteService.generarReporteBienesAsignadosEmpleados(formato), "bienes-asignados.csv", MediaType.APPLICATION_OCTET_STREAM);
    }

    private ResponseEntity<byte[]> asAttachment(byte[] body, String filename, MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(body);
    }
}
