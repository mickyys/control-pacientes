using System;
using System.Collections.Generic;

namespace ControlPacientes.Core.Models;

/// <summary>
/// Representa un paciente en el sistema
/// </summary>
public class Paciente
{
    public int Id { get; set; }
    
    /// <summary>
    /// RUT del paciente (ej: 12345678-K)
    /// </summary>
    public string Rut { get; set; } = string.Empty;
    
    public string Nombre { get; set; } = string.Empty;
    
    public string Apellido { get; set; } = string.Empty;
    
    public DateTime FechaNacimiento { get; set; }
    
    /// <summary>
    /// Correo electrónico del paciente
    /// </summary>
    public string Email { get; set; } = string.Empty;
    
    /// <summary>
    /// Teléfono del paciente
    /// </summary>
    public string Telefono { get; set; } = string.Empty;
    
    /// <summary>
    /// Ciudad de residencia
    /// </summary>
    public string Ciudad { get; set; } = string.Empty;
    
    public string Direccion { get; set; } = string.Empty;
    
    /// <summary>
    /// Información de alergias o condiciones especiales
    /// </summary>
    public string NotasClinicas { get; set; } = string.Empty;
    
    public DateTime FechaRegistro { get; set; }
    
    public DateTime? FechaActualizacion { get; set; }
    
    /// <summary>
    /// Colección de fichas médicas del paciente
    /// </summary>
    public virtual ICollection<FichaMedica> FichasMedicas { get; set; } = new List<FichaMedica>();
    
    public string NombreCompleto => $"{Nombre} {Apellido}";
}
