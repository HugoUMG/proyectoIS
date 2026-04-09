package com.empresa.gestionactivos.repository;

import com.empresa.gestionactivos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRol(Usuario.RolUsuario rol);
}
