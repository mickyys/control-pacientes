using System;
using System.Collections.Generic;

namespace ControlPacientes.Core.Models;

/// <summary>
/// Representa una ficha médica de una atención a un paciente
/// </summary>
public class FichaMedica
{
    public int Id { get; set; }
    
    public int PacienteId { get; set; }
    
    /// <summary>
    /// Fecha de la atención médica
    /// </summary>
    public DateTime FechaAtencion { get; set; }
    
    /// <summary>
    /// Profesional de salud que realizó la atención
    /// </summary>
    public string ProfesionalNombre { get; set; } = string.Empty;
    
    /// <summary>
    /// Motivo de la consulta
    /// </summary>
    public string MotivoConsulta { get; set; } = string.Empty;
    
    /// <summary>
    /// Diagnóstico realizado
    /// </summary>
    public string Diagnostico { get; set; } = string.Empty;
    
    /// <summary>
    /// Tratamiento recomendado
    /// </summary>
    public string Tratamiento { get; set; } = string.Empty;
    
    /// <summary>
    /// Notas adicionales
    /// </summary>
    public string Notas { get; set; } = string.Empty;
    
    public DateTime FechaCreacion { get; set; }
    
    public DateTime? FechaActualizacion { get; set; }
    
    /// <summary>
    /// Navegación hacia el paciente
    /// </summary>
    public virtual Paciente? Paciente { get; set; }
    
    /// <summary>
    /// Medicamentos asociados a esta ficha
    /// </summary>
    public virtual ICollection<MedicamentoAtencion> Medicamentos { get; set; } = new List<MedicamentoAtencion>();
}
