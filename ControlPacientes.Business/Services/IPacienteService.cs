using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Interfaz para servicio de Pacientes
/// </summary>
public interface IPacienteService
{
    Task<Paciente> CrearPacienteAsync(Paciente paciente);
    
    Task<Paciente> ActualizarPacienteAsync(Paciente paciente);
    
    Task<Paciente?> ObtenerPacienteAsync(int id);
    
    Task<Paciente?> ObtenerPacienteConFichasAsync(int id);
    
    Task<Paciente?> BuscarPorRutAsync(string rut);
    
    Task<IEnumerable<Paciente>> BuscarPacientesAsync(string? searchTerm, string? ciudad = null);
    
    Task<IEnumerable<Paciente>> ObtenerTodosPacientesAsync();
    
    Task EliminarPacienteAsync(int id);
    
    Task<IEnumerable<string>> ObtenerCiudadesAsync();
    
    Task<int> ContarPacientesAsync();
}
