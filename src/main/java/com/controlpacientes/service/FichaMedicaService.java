package com.controlpacientes.service;

import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.repository.FichaMedicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FichaMedicaService {

    private final FichaMedicaRepository fichaMedicaRepository;

    @Transactional(readOnly = true)
    public List<FichaMedica> findAll() {
        List<FichaMedica> fichas = fichaMedicaRepository.findAll();
        // Inicializar lazy-loaded relationships dentro de la transacción
        fichas.forEach(ficha -> {
            Hibernate.initialize(ficha.getPaciente());
            Hibernate.initialize(ficha.getMedicamentos());
        });
        return fichas;
    }

    @Transactional(readOnly = true)
    public List<FichaMedica> findAllOrderByFechaDesc() {
        List<FichaMedica> fichas = fichaMedicaRepository.findAllByOrderByFechaAtencionDesc();
        // Inicializar lazy-loaded relationships dentro de la transacción
        fichas.forEach(ficha -> {
            Hibernate.initialize(ficha.getPaciente());
            Hibernate.initialize(ficha.getMedicamentos());
        });
        return fichas;
    }

    @Transactional(readOnly = true)
    public List<FichaMedica> findByPacienteId(Long pacienteId) {
        List<FichaMedica> fichas = fichaMedicaRepository.findByPacienteIdOrderByFechaAtencionDesc(pacienteId);
        // Inicializar lazy-loaded relationships dentro de la transacción
        fichas.forEach(ficha -> {
            Hibernate.initialize(ficha.getPaciente());
            Hibernate.initialize(ficha.getMedicamentos());
        });
        return fichas;
    }

    @Transactional(readOnly = true)
    public Optional<FichaMedica> findById(Long id) {
        Optional<FichaMedica> ficha = fichaMedicaRepository.findById(id);
        // Inicializar lazy-loaded relationships dentro de la transacción
        ficha.ifPresent(f -> {
            Hibernate.initialize(f.getPaciente());
            Hibernate.initialize(f.getMedicamentos());
        });
        return ficha;
    }

    @Transactional
    public FichaMedica save(FichaMedica fichaMedica) {
        // Ensure medications are linked to the ficha
        if (fichaMedica.getMedicamentos() != null) {
            fichaMedica.getMedicamentos().forEach(m -> m.setFichaMedica(fichaMedica));
        }
        FichaMedica saved = fichaMedicaRepository.save(fichaMedica);
        log.info("FICHA_CREADA - ID: {}, Paciente: {}, Fecha: {}", 
            saved.getId(), 
            saved.getPaciente().getNombre() + " " + saved.getPaciente().getApellido(), 
            saved.getFechaAtencion());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        fichaMedicaRepository.deleteById(id);
        log.info("FICHA_ELIMINADA - ID: {}", id);
    }
}
