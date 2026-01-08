package com.controlpacientes.repository;

import com.controlpacientes.model.MedicamentoAtencion;
import com.controlpacientes.model.FichaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoAtencionRepository extends JpaRepository<MedicamentoAtencion, Long> {
    List<MedicamentoAtencion> findByFichaMedica(FichaMedica fichaMedica);
}
