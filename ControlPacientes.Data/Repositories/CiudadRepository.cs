using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Context;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Repositorio específico para Ciudades
/// </summary>
public class CiudadRepository : GenericRepository<Ciudad>, ICiudadRepository
{
    public CiudadRepository(ApplicationDbContext context) : base(context)
    {
    }

    public async Task<Ciudad?> GetByNombreAsync(string nombre)
    {
        return await DbSet.FirstOrDefaultAsync(c => c.Nombre.ToLower() == nombre.ToLower());
    }

    public async Task<IEnumerable<Ciudad>> SearchAsync(string searchTerm)
    {
        if (string.IsNullOrWhiteSpace(searchTerm))
            return await DbSet.OrderBy(c => c.Nombre).ToListAsync();

        return await DbSet
            .Where(c => c.Nombre.ToLower().Contains(searchTerm.ToLower()))
            .OrderBy(c => c.Nombre)
            .ToListAsync();
    }
}
