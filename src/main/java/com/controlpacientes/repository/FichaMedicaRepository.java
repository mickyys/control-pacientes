package com.controlpacientes.repository;

import com.controlpacientes.model.FichaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FichaMedicaRepository extends JpaRepository<FichaMedica, Long> {
    List<FichaMedica> findByPacienteIdOrderByFechaAtencionDesc(Long pacienteId);
    
    List<FichaMedica> findAllByOrderByFechaAtencionDesc();
}
