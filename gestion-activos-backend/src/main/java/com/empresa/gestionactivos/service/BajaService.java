package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.BajaDTO;
import com.empresa.gestionactivos.dto.AprobacionBajaDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.*;
import com.empresa.gestionactivos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BajaService {
    
    private final BajaRepository bajaRepository;
    private final ActivoRepository activoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AprobacionBajaRepository aprobacionRepository;
    
    private static final int TOTAL_NIVELES_APROBACION = 3;
    
    public BajaDTO crearSolicitudBaja(BajaDTO dto) {
        // Validar que el activo exista y no esté ya dado de baja
        Activo activo = activoRepository.findById(dto.getActivoId())
            .orElseThrow(() -> new BusinessException("Activo no encontrado"));
        
        if (activo.getEstado() == Activo.EstadoActivo.DADO_DE_BAJA) {
            throw new BusinessException("El activo ya está dado de baja");
        }
        
        if (bajaRepository.existsByActivoIdAndEstadoIn(activo.getId(), 
                List.of(Baja.EstadoBaja.PENDIENTE_APROBACION, Baja.EstadoBaja.APROBADA))) {
            throw new BusinessException("Ya existe una solicitud de baja activa para este activo");
        }
        
        // Obtener usuario actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario solicitante = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        // Crear solicitud
        Baja baja = new Baja();
        baja.setActivo(activo);
        baja.setTipoBaja(dto.getTipoBaja());
        baja.setMotivoBaja(dto.getMotivoBaja());
        baja.setJustificacion(dto.getJustificacion());
        baja.setSolicitadoPor(solicitante);
        baja.setEstado(Baja.EstadoBaja.PENDIENTE_APROBACION);
        baja.setValorResidual(calcularValorResidual(activo));
        
        Baja saved = bajaRepository.save(baja);
        
        // Crear flujo de aprobación
        crearFlujoAprobacion(saved);
        
        log.info("Solicitud de baja creada para activo ID: {} por usuario: {}", 
            activo.getId(), username);
        
        return mapToDTO(saved);
    }
    
    private void crearFlujoAprobacion(Baja baja) {
        // Nivel 1: Jefe de Departamento (rol ENCARGADO_INVENTARIO o ADMINISTRADOR)
        Usuario jefeDepartamento = obtenerJefeDepartamento(baja.getSolicitadoPor());
        crearAprobacion(baja, jefeDepartamento, 1);
        
        // Nivel 2: Gerencia (rol GERENCIA_FINANZAS)
        Usuario gerente = obtenerGerente();
        crearAprobacion(baja, gerente, 2);
        
        // Nivel 3: Dirección Finanzas (rol GERENCIA_FINANZAS con mayor jerarquía)
        Usuario directorFinanzas = obtenerDirectorFinanzas();
        crearAprobacion(baja, directorFinanzas, 3);
    }
    
    private void crearAprobacion(Baja baja, Usuario aprobador, Integer nivel) {
        AprobacionBaja aprobacion = new AprobacionBaja();
        aprobacion.setBaja(baja);
        aprobacion.setAprobador(aprobador);
        aprobacion.setNivelJerarquico(nivel);
        aprobacion.setDecision(AprobacionBaja.DecisionAprobacion.PENDIENTE);
        aprobacionRepository.save(aprobacion);
    }
    
    public BajaDTO aprobarBaja(Long bajaId, Integer nivel, String comentarios) {
        Baja baja = bajaRepository.findById(bajaId)
            .orElseThrow(() -> new BusinessException("Solicitud de baja no encontrada"));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario aprobador = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        // Buscar la aprobación correspondiente
        AprobacionBaja aprobacion = aprobacionRepository
            .findByBajaIdAndNivelJerarquico(bajaId, nivel)
            .orElseThrow(() -> new BusinessException("Nivel de aprobación no encontrado"));
        
        // Validar que el usuario tenga permiso para aprobar este nivel
        if (!aprobacion.getAprobador().getId().equals(aprobador.getId()) && 
            !aprobador.getRol().equals(Usuario.RolUsuario.ADMINISTRADOR)) {
            throw new BusinessException("No tiene permisos para aprobar este nivel");
        }
        
        aprobacion.setDecision(AprobacionBaja.DecisionAprobacion.APROBADO);
        aprobacion.setComentarios(comentarios);
        aprobacion.setFechaAprobacion(LocalDate.now());
        aprobacionRepository.save(aprobacion);
        
        // Verificar si todos los niveles están aprobados
        if (todosNivelesAprobados(bajaId)) {
            baja.setEstado(Baja.EstadoBaja.APROBADA);
            bajaRepository.save(baja);
            log.info("Baja ID: {} completamente aprobada", bajaId);
        }
        
        return mapToDTO(baja);
    }
    
    private boolean todosNivelesAprobados(Long bajaId) {
        List<AprobacionBaja> aprobaciones = aprobacionRepository.findByBajaId(bajaId);
        return aprobaciones.stream()
            .allMatch(a -> a.getDecision() == AprobacionBaja.DecisionAprobacion.APROBADO) &&
            aprobaciones.size() == TOTAL_NIVELES_APROBACION;
    }
    
    public BajaDTO ejecutarBaja(Long bajaId, BigDecimal valorVenta, String comprobante) {
        Baja baja = bajaRepository.findById(bajaId)
            .orElseThrow(() -> new BusinessException("Solicitud de baja no encontrada"));
        
        if (baja.getEstado() != Baja.EstadoBaja.APROBADA) {
            throw new BusinessException("La baja debe estar aprobada para ejecutarse");
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario ejecutor = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        // Actualizar estado del activo
        Activo activo = baja.getActivo();
        activo.setEstado(Activo.EstadoActivo.DADO_DE_BAJA);
        activoRepository.save(activo);
        
        // Actualizar baja
        baja.setEstado(Baja.EstadoBaja.EJECUTADA);
        baja.setFechaEjecucion(LocalDate.now());
        baja.setEjecutadoPor(ejecutor);
        
        if (baja.getTipoBaja() == Baja.TipoBaja.VENTA && valorVenta != null) {
            baja.setValorVenta(valorVenta);
            baja.setComprobanteVenta(comprobante);
        }
        
        Baja saved = bajaRepository.save(baja);
        log.info("Baja ID: {} ejecutada por usuario: {}", bajaId, username);
        
        return mapToDTO(saved);
    }
    
    private BigDecimal calcularValorResidual(Activo activo) {
        // Lógica de depreciación lineal
        if (activo.getVidaUtilMeses() == null || activo.getFechaAdquisicion() == null) {
            return activo.getValorCompra();
        }
        
        long mesesTranscurridos = java.time.temporal.ChronoUnit.MONTHS.between(
            activo.getFechaAdquisicion(), LocalDate.now());
        
        if (mesesTranscurridos >= activo.getVidaUtilMeses()) {
            return BigDecimal.ONE; // Valor simbólico de $1
        }
        
        BigDecimal depreciacionMensual = activo.getValorCompra()
            .divide(BigDecimal.valueOf(activo.getVidaUtilMeses()), 2, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal depreciacionAcumulada = depreciacionMensual
            .multiply(BigDecimal.valueOf(mesesTranscurridos));
        
        return activo.getValorCompra().subtract(depreciacionAcumulada).max(BigDecimal.ONE);
    }
    
    // Métodos auxiliares para obtener aprobadores (simplificados)
    private Usuario obtenerJefeDepartamento(Usuario solicitante) {
        // En producción, esto vendría de la estructura organizacional
        return usuarioRepository.findByRol(Usuario.RolUsuario.ADMINISTRADOR)
            .stream().findFirst()
            .orElseThrow(() -> new BusinessException("No hay administrador disponible"));
    }
    
    private Usuario obtenerGerente() {
        return usuarioRepository.findByRol(Usuario.RolUsuario.GERENCIA_FINANZAS)
            .stream().findFirst()
            .orElseThrow(() -> new BusinessException("No hay gerente disponible"));
    }
    
    private Usuario obtenerDirectorFinanzas() {
        return usuarioRepository.findByRol(Usuario.RolUsuario.GERENCIA_FINANZAS)
            .stream().skip(1).findFirst()
            .orElseThrow(() -> new BusinessException("No hay director de finanzas disponible"));
    }
}