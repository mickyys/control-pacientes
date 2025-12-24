using System;

namespace ControlPacientes.Core.Models;

/// <summary>
/// Representa un medicamento asociado a una atención
/// </summary>
public class MedicamentoAtencion
{
    public int Id { get; set; }
    
    public int FichaMedicaId { get; set; }
    
    /// <summary>
    /// Nombre del medicamento
    /// </summary>
    public string NombreMedicamento { get; set; } = string.Empty;
    
    /// <summary>
    /// Dosis prescrita
    /// </summary>
    public string Dosis { get; set; } = string.Empty;
    
    /// <summary>
    /// Frecuencia de administración (ej: 3 veces al día)
    /// </summary>
    public string Frecuencia { get; set; } = string.Empty;
    
    /// <summary>
    /// Duración del tratamiento
    /// </summary>
    public string Duracion { get; set; } = string.Empty;
    
    /// <summary>
    /// Indicaciones especiales
    /// </summary>
    public string Indicaciones { get; set; } = string.Empty;
    
    public DateTime FechaCreacion { get; set; }
    
    /// <summary>
    /// Navegación hacia la ficha médica
    /// </summary>
    public virtual FichaMedica? FichaMedica { get; set; }
}
