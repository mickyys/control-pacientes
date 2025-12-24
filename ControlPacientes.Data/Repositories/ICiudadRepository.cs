using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Interfaz para repositorio de Ciudades
/// </summary>
public interface ICiudadRepository : IGenericRepository<Ciudad>
{
    Task<Ciudad?> GetByNombreAsync(string nombre);
    Task<IEnumerable<Ciudad>> SearchAsync(string searchTerm);
}
