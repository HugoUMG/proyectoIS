package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.model.Activo;
import com.empresa.gestionactivos.model.Asignacion;
import com.empresa.gestionactivos.model.Adquisicion;
import com.empresa.gestionactivos.repository.ActivoRepository;
import com.empresa.gestionactivos.repository.AdquisicionRepository;
import com.empresa.gestionactivos.repository.AsignacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final ActivoRepository activoRepository;
    private final AsignacionRepository asignacionRepository;
    private final AdquisicionRepository adquisicionRepository;

    public byte[] generarHojaVidaEmpleado(Long empleadoId) {
        List<Asignacion> asignaciones = asignacionRepository.findByUsuarioResponsableId(empleadoId);
        StringBuilder sb = new StringBuilder("Hoja de vida del empleado " + empleadoId + "\n");
        for (Asignacion a : asignaciones) {
            sb.append(a.getId()).append(",")
                .append(a.getActivo() != null ? a.getActivo().getCodigoIdentificacion() : "-")
                .append(",").append(a.getEstado()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generarReporteCentroCosto(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Activo> activos = activoRepository.findAll();
        StringBuilder sb = new StringBuilder("centroCosto,activo,valor\n");
        for (Activo a : activos) {
            sb.append(a.getUbicacionFisica()).append(",").append(a.getNombre()).append(",").append(a.getValorCompra()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generarReporteDepreciacion() {
        List<Activo> activos = activoRepository.findActivosDepreciables();
        StringBuilder sb = new StringBuilder("codigo,valorCompra,vidaUtilMeses\n");
        for (Activo a : activos) {
            sb.append(a.getCodigoIdentificacion()).append(",").append(a.getValorCompra()).append(",").append(a.getVidaUtilMeses()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generarReporteProximosBaja(String formato) {
        List<Activo> activos = activoRepository.findActivosProximosABaja();
        StringBuilder sb = new StringBuilder("codigo,nombre,vidaUtilMeses\n");
        for (Activo a : activos) {
            sb.append(a.getCodigoIdentificacion()).append(",").append(a.getNombre()).append(",").append(a.getVidaUtilMeses()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generarReporteBienesInvertidos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Adquisicion> adquisiciones = adquisicionRepository.findByFechaFacturaBetween(fechaInicio, fechaFin);
        StringBuilder sb = new StringBuilder("factura,fecha,total\n");
        for (Adquisicion a : adquisiciones) {
            sb.append(a.getNumeroFactura()).append(",").append(a.getFechaFactura()).append(",").append(a.getTotal()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generarReporteBienesAsignadosEmpleados(String formato) {
        List<Asignacion> asignaciones = asignacionRepository.findByEstado(Asignacion.EstadoAsignacion.ACTIVA);
        StringBuilder sb = new StringBuilder("empleado,activo,fecha\n");
        for (Asignacion a : asignaciones) {
            sb.append(a.getUsuarioResponsable() != null ? a.getUsuarioResponsable().getNombreCompleto() : "-")
                .append(",")
                .append(a.getActivo() != null ? a.getActivo().getNombre() : "-")
                .append(",")
                .append(a.getFechaAsignacion())
                .append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
