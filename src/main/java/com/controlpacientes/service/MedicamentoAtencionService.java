package com.controlpacientes.service;

import com.controlpacientes.model.MedicamentoAtencion;
import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.repository.MedicamentoAtencionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoAtencionService {

    private final MedicamentoAtencionRepository medicamentoAtencionRepository;

    public MedicamentoAtencion save(MedicamentoAtencion medicamento) {
        return medicamentoAtencionRepository.save(medicamento);
    }

    public List<MedicamentoAtencion> findByFichaMedica(FichaMedica fichaMedica) {
        return medicamentoAtencionRepository.findByFichaMedica(fichaMedica);
    }

    public MedicamentoAtencion findById(Long id) {
        return medicamentoAtencionRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        medicamentoAtencionRepository.deleteById(id);
    }

    public List<MedicamentoAtencion> findAll() {
        return medicamentoAtencionRepository.findAll();
    }
}
