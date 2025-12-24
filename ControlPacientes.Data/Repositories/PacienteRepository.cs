using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Context;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Repositorio específico para Pacientes
/// </summary>
public class PacienteRepository : GenericRepository<Paciente>, IPacienteRepository
{
    public PacienteRepository(ApplicationDbContext context) : base(context)
    {
    }

    public async Task<Paciente?> GetByRutAsync(string rut)
    {
        return await DbSet.FirstOrDefaultAsync(p => p.Rut == rut);
    }

    public async Task<IEnumerable<Paciente>> SearchAsync(string searchTerm, string? ciudad = null)
    {
        var query = DbSet.AsQueryable();

        if (!string.IsNullOrWhiteSpace(searchTerm))
        {
            query = query.Where(p => 
                p.Rut.Contains(searchTerm) ||
                p.Nombre.Contains(searchTerm) ||
                p.Apellido.Contains(searchTerm));
        }

        if (!string.IsNullOrWhiteSpace(ciudad))
        {
            query = query.Where(p => p.Ciudad == ciudad);
        }

        return await query.OrderBy(p => p.Nombre).ToListAsync();
    }

    public async Task<IEnumerable<Paciente>> GetByNombreAsync(string nombre)
    {
        return await DbSet
            .Where(p => p.Nombre.Contains(nombre) || p.Apellido.Contains(nombre))
            .OrderBy(p => p.Nombre)
            .ToListAsync();
    }

    public async Task<IEnumerable<Paciente>> GetByCiudadAsync(string ciudad)
    {
        return await DbSet
            .Where(p => p.Ciudad == ciudad)
            .OrderBy(p => p.Nombre)
            .ToListAsync();
    }

    public async Task<IEnumerable<string>> GetCiudadesAsync()
    {
        return await DbSet
            .Select(p => p.Ciudad)
            .Distinct()
            .OrderBy(c => c)
            .ToListAsync();
    }

    public async Task<Paciente?> GetWithFichasAsync(int pacienteId)
    {
        return await DbSet
            .Include(p => p.FichasMedicas)
            .FirstOrDefaultAsync(p => p.Id == pacienteId);
    }
}
