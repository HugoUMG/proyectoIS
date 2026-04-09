package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.dto.AsignacionDTO;
import com.empresa.gestionactivos.exception.BusinessException;
import com.empresa.gestionactivos.model.Activo;
import com.empresa.gestionactivos.model.Usuario;
import com.empresa.gestionactivos.repository.ActivoRepository;
import com.empresa.gestionactivos.repository.AsignacionRepository;
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
class AsignacionServiceTest {

    @Mock AsignacionRepository asignacionRepository;
    @Mock ActivoRepository activoRepository;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks AsignacionService asignacionService;

    @Test
    void noDebeAsignarActivoDadoDeBaja() {
        Activo activo = new Activo();
        activo.setId(1L);
        activo.setEstado(Activo.EstadoActivo.DADO_DE_BAJA);

        when(activoRepository.findById(1L)).thenReturn(Optional.of(activo));

        AsignacionDTO dto = new AsignacionDTO();
        dto.setActivoId(1L);
        dto.setUsuarioResponsableId(2L);
        dto.setAsignadoPorId(3L);

        assertThrows(BusinessException.class, () -> asignacionService.crearAsignacion(dto));
    }

    @Test
    void debeExigirUsuariosExistentes() {
        Activo activo = new Activo();
        activo.setId(1L);
        activo.setEstado(Activo.EstadoActivo.DISPONIBLE);
        when(activoRepository.findById(1L)).thenReturn(Optional.of(activo));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        AsignacionDTO dto = new AsignacionDTO();
        dto.setActivoId(1L);
        dto.setUsuarioResponsableId(2L);
        dto.setAsignadoPorId(3L);

        assertThrows(BusinessException.class, () -> asignacionService.crearAsignacion(dto));
    }
}
