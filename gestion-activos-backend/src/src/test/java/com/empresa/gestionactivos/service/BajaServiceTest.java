package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.BajaDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Activo;
import com.empresa.gestionactivos.repository.ActivoRepository;
import com.empresa.gestionactivos.repository.AprobacionBajaRepository;
import com.empresa.gestionactivos.repository.BajaRepository;
import com.empresa.gestionactivos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BajaServiceTest {

    @Mock BajaRepository bajaRepository;
    @Mock ActivoRepository activoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock AprobacionBajaRepository aprobacionRepository;

    @InjectMocks BajaService bajaService;

    @Test
    void noDebeCrearSolicitudSiActivoYaEstaDeBaja() {
        Activo activo = new Activo();
        activo.setId(10L);
        activo.setEstado(Activo.EstadoActivo.DADO_DE_BAJA);

        when(activoRepository.findById(10L)).thenReturn(Optional.of(activo));

        BajaDTO dto = new BajaDTO();
        dto.setActivoId(10L);

        assertThrows(BusinessException.class, () -> bajaService.crearSolicitudBaja(dto));
    }
}
