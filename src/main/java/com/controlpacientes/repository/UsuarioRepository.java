package com.controlpacientes.repository;

import com.controlpacientes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByRut(String rut);
    
    List<Usuario> findByActivo(boolean activo);
}
