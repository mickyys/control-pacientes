package com.controlpacientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicamentos_atencion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentoAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_medica_id")
    private FichaMedica fichaMedica;

    private String nombreMedicamento;

    private String dosis;

    private String frecuencia;

    private String duracion;

    private Integer cantidadRecetar;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
