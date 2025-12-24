using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Interfaz para servicio de Ciudades
/// </summary>
public interface ICiudadService
{
    Task<IEnumerable<Ciudad>> ObtenerTodasAsync();
    Task<IEnumerable<Ciudad>> BuscarCiudadesAsync(string searchTerm);
    Task<Ciudad> AsegurarCiudadExisteAsync(string nombre);
}
