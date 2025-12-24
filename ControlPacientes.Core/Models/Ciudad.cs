using System;

namespace ControlPacientes.Core.Models;

/// <summary>
/// Representa una ciudad en el sistema
/// </summary>
public class Ciudad
{
    public int Id { get; set; }
    
    /// <summary>
    /// Nombre de la ciudad
    /// </summary>
    public string Nombre { get; set; } = string.Empty;
}
