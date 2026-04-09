package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.AprobacionBajaDTO;
import com.empresa.gestionactivos.dto.BajaDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.*;
import com.empresa.gestionactivos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BajaService {
    private final BajaRepository bajaRepository;
    private final ActivoRepository activoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AprobacionBajaRepository aprobacionRepository;

    public BajaDTO crearSolicitudBaja(BajaDTO dto) {
        Activo activo = activoRepository.findById(dto.getActivoId()).orElseThrow(() -> new BusinessException("Activo no encontrado"));
        if (activo.getEstado() == Activo.EstadoActivo.DADO_DE_BAJA) {
            throw new BusinessException("El activo ya está dado de baja");
        }

        Usuario solicitante = getCurrentUser();
        Baja baja = new Baja();
        baja.setActivo(activo);
        baja.setTipoBaja(dto.getTipoBaja());
        baja.setMotivoBaja(dto.getMotivoBaja());
        baja.setJustificacion(dto.getJustificacion());
        baja.setSolicitadoPor(solicitante);
        baja.setEstado(Baja.EstadoBaja.PENDIENTE_APROBACION);
        baja.setValorResidual(dto.getValorResidual() != null ? dto.getValorResidual() : activo.getValorActual());
        Baja saved = bajaRepository.save(baja);

        crearAprobacion(saved, 1);
        crearAprobacion(saved, 2);
        crearAprobacion(saved, 3);
        return toDto(saved);
    }

    private void crearAprobacion(Baja baja, int nivel) {
        Usuario aprobador = usuarioRepository.findByRol(Usuario.RolUsuario.GERENCIA_FINANZAS).stream().findFirst()
            .orElseGet(() -> usuarioRepository.findByRol(Usuario.RolUsuario.ADMINISTRADOR).stream().findFirst().orElseThrow(() -> new BusinessException("No hay aprobadores")));
        AprobacionBaja ap = new AprobacionBaja();
        ap.setBaja(baja);
        ap.setAprobador(aprobador);
        ap.setNivelJerarquico(nivel);
        ap.setDecision(AprobacionBaja.DecisionAprobacion.PENDIENTE);
        aprobacionRepository.save(ap);
    }

    public List<BajaDTO> listarBajasPendientesAprobacion() {
        return bajaRepository.findByEstado(Baja.EstadoBaja.PENDIENTE_APROBACION).stream().map(this::toDto).toList();
    }

    public List<AprobacionBajaDTO> obtenerAprobacionesPendientesUsuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getName()
            : "anonymousUser";
        return aprobacionRepository.findByAprobadorUsernameAndDecision(username, AprobacionBaja.DecisionAprobacion.PENDIENTE)
            .stream().map(this::toDto).toList();
    }

    public BajaDTO aprobarBaja(Long bajaId, Integer nivel, String comentarios) {
        AprobacionBaja ap = aprobacionRepository.findByBajaIdAndNivelJerarquico(bajaId, nivel)
            .orElseThrow(() -> new BusinessException("Nivel no encontrado"));
        ap.setDecision(AprobacionBaja.DecisionAprobacion.APROBADO);
        ap.setComentarios(comentarios);
        ap.setFechaAprobacion(LocalDate.now());
        aprobacionRepository.save(ap);

        Baja baja = ap.getBaja();
        boolean allApproved = aprobacionRepository.findByBajaId(bajaId).stream()
            .allMatch(a -> a.getDecision() == AprobacionBaja.DecisionAprobacion.APROBADO);
        if (allApproved) {
            baja.setEstado(Baja.EstadoBaja.APROBADA);
            bajaRepository.save(baja);
        }
        return toDto(baja);
    }

    public BajaDTO rechazarBaja(Long bajaId, Integer nivel, String motivo) {
        AprobacionBaja ap = aprobacionRepository.findByBajaIdAndNivelJerarquico(bajaId, nivel)
            .orElseThrow(() -> new BusinessException("Nivel no encontrado"));
        ap.setDecision(AprobacionBaja.DecisionAprobacion.RECHAZADO);
        ap.setComentarios(motivo);
        ap.setFechaAprobacion(LocalDate.now());
        aprobacionRepository.save(ap);

        Baja baja = ap.getBaja();
        baja.setEstado(Baja.EstadoBaja.RECHAZADA);
        bajaRepository.save(baja);
        return toDto(baja);
    }

    public BajaDTO ejecutarBaja(Long bajaId, BigDecimal valorVenta, String comprobante) {
        Baja baja = bajaRepository.findById(bajaId).orElseThrow(() -> new BusinessException("Baja no encontrada"));
        if (baja.getEstado() != Baja.EstadoBaja.APROBADA) {
            throw new BusinessException("Solo una baja aprobada puede ejecutarse");
        }
        baja.setEstado(Baja.EstadoBaja.EJECUTADA);
        baja.setFechaEjecucion(LocalDate.now());
        baja.setValorVenta(valorVenta);
        baja.setComprobanteVenta(comprobante);
        Activo activo = baja.getActivo();
        activo.setEstado(Activo.EstadoActivo.DADO_DE_BAJA);
        activoRepository.save(activo);
        return toDto(bajaRepository.save(baja));
    }

    public List<BajaDTO> obtenerHistorialBajasPorActivo(Long activoId) {
        return bajaRepository.findByActivoId(activoId).stream().map(this::toDto).toList();
    }

    public List<AprobacionBajaDTO> consultarFlujoAprobacion(Long id) {
        return aprobacionRepository.findByBajaId(id).stream().map(this::toDto).toList();
    }

    private Usuario getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getName()
            : null;
        if (username == null || username.equals("anonymousUser")) {
            return usuarioRepository.findAll().stream().findFirst().orElseThrow(() -> new BusinessException("No hay usuarios"));
        }
        return usuarioRepository.findByUsername(username).orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    private BajaDTO toDto(Baja b) {
        BajaDTO dto = new BajaDTO();
        dto.setId(b.getId());
        dto.setActivoId(b.getActivo() != null ? b.getActivo().getId() : null);
        dto.setTipoBaja(b.getTipoBaja());
        dto.setMotivoBaja(b.getMotivoBaja());
        dto.setJustificacion(b.getJustificacion());
        dto.setValorResidual(b.getValorResidual());
        dto.setFechaSolicitud(b.getFechaSolicitud());
        dto.setSolicitadoPorId(b.getSolicitadoPor() != null ? b.getSolicitadoPor().getId() : null);
        dto.setEstado(b.getEstado());
        dto.setFechaEjecucion(b.getFechaEjecucion());
        dto.setEjecutadoPorId(b.getEjecutadoPor() != null ? b.getEjecutadoPor().getId() : null);
        dto.setValorVenta(b.getValorVenta());
        dto.setComprobanteVenta(b.getComprobanteVenta());
        dto.setObservaciones(b.getObservaciones());
        return dto;
    }

    private AprobacionBajaDTO toDto(AprobacionBaja a) {
        AprobacionBajaDTO dto = new AprobacionBajaDTO();
        dto.setId(a.getId());
        dto.setBajaId(a.getBaja() != null ? a.getBaja().getId() : null);
        dto.setNivelJerarquico(a.getNivelJerarquico());
        dto.setAprobadorId(a.getAprobador() != null ? a.getAprobador().getId() : null);
        dto.setAprobadorNombre(a.getAprobador() != null ? a.getAprobador().getNombreCompleto() : null);
        dto.setDecision(a.getDecision());
        dto.setComentarios(a.getComentarios());
        dto.setFechaAprobacion(a.getFechaAprobacion());
        return dto;
    }
}
