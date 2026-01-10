package com.controlpacientes.service;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.repository.PacienteRepository;
import com.controlpacientes.util.RutUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<Paciente> findAll() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        // Inicializar lazy-loaded relationships dentro de la transacción
        pacientes.forEach(p -> Hibernate.initialize(p.getFichasMedicas()));
        return pacientes;
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> findById(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        // Inicializar lazy-loaded relationships dentro de la transacción
        paciente.ifPresent(p -> Hibernate.initialize(p.getFichasMedicas()));
        return paciente;
    }

    @Transactional(readOnly = true)
    public List<Paciente> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        List<Paciente> pacientes = pacienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRutContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCiudadContainingIgnoreCase(query, query, query, query, query);
        // Inicializar lazy-loaded relationships dentro de la transacción
        pacientes.forEach(p -> Hibernate.initialize(p.getFichasMedicas()));
        return pacientes;
    }

    @Transactional(readOnly = true)
    public List<Paciente> searchAdvanced(String nombre, String rut, String email, String ciudad) {
        List<Paciente> allPacientes = findAll();
        
        return allPacientes.stream()
                .filter(p -> nombre == null || nombre.isBlank() || 
                       p.getNombre().toLowerCase().contains(nombre.toLowerCase()) ||
                       p.getApellido().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> rut == null || rut.isBlank() || 
                       p.getRut().toLowerCase().contains(rut.toLowerCase()))
                .filter(p -> email == null || email.isBlank() || 
                       (p.getEmail() != null && p.getEmail().toLowerCase().contains(email.toLowerCase())))
                .filter(p -> ciudad == null || ciudad.isBlank() || 
                       (p.getCiudad() != null && p.getCiudad().toLowerCase().contains(ciudad.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Paciente save(Paciente paciente) {
        if (!RutUtils.validateRut(paciente.getRut())) {
            throw new IllegalArgumentException("RUT inválido");
        }
        
        paciente.setRut(paciente.getRut().replace(".", "").replace("-", "").toUpperCase());
        
        // Capitalizar nombre y apellido
        paciente.setNombre(RutUtils.capitalize(paciente.getNombre()));
        paciente.setApellido(RutUtils.capitalize(paciente.getApellido()));
        
        // Check if RUT already exists for a different patient
        Optional<Paciente> existing = pacienteRepository.findByRut(paciente.getRut());
        if (existing.isPresent() && !existing.get().getId().equals(paciente.getId())) {
            throw new IllegalStateException("El RUT ya está registrado");
        }
        
        Paciente saved = pacienteRepository.save(paciente);
        
        // Log del evento
        if (paciente.getId() == null) {
            log.info("PACIENTE_CREADO - RUT: {}, Nombre: {} {}", 
                saved.getRut(), 
                saved.getNombre(), 
                saved.getApellido());
        } else {
            log.info("PACIENTE_ACTUALIZADO - RUT: {}, Nombre: {} {}", 
                saved.getRut(), 
                saved.getNombre(), 
                saved.getApellido());
        }
        
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        pacienteRepository.deleteById(id);
        paciente.ifPresent(p -> log.info("PACIENTE_ELIMINADO - RUT: {}, Nombre: {} {}", 
            p.getRut(), p.getNombre(), p.getApellido()));
    }
}
