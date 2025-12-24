using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Interfaz para repositorio de Pacientes
/// </summary>
public interface IPacienteRepository : IGenericRepository<Paciente>
{
    Task<Paciente?> GetByRutAsync(string rut);
    
    Task<IEnumerable<Paciente>> SearchAsync(string searchTerm, string? ciudad = null);
    
    Task<IEnumerable<Paciente>> GetByNombreAsync(string nombre);
    
    Task<IEnumerable<Paciente>> GetByCiudadAsync(string ciudad);
    
    Task<IEnumerable<string>> GetCiudadesAsync();
    
    Task<Paciente?> GetWithFichasAsync(int pacienteId);
}
