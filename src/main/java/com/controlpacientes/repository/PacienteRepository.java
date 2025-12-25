package com.controlpacientes.repository;

import com.controlpacientes.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByRut(String rut);
    List<Paciente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    List<Paciente> findByCiudadContainingIgnoreCase(String ciudad);
    List<Paciente> findByEmailContainingIgnoreCase(String email);
    List<Paciente> findByRutContainingIgnoreCase(String rut);
    List<Paciente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRutContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCiudadContainingIgnoreCase(String nombre, String apellido, String rut, String email, String ciudad);
}
