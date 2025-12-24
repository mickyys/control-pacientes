using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Context;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Repositorio específico para Fichas Médicas
/// </summary>
public class FichaMedicaRepository : GenericRepository<FichaMedica>, IFichaMedicaRepository
{
    public FichaMedicaRepository(ApplicationDbContext context) : base(context)
    {
    }

    public async Task<IEnumerable<FichaMedica>> GetByPacienteAsync(int pacienteId)
    {
        return await DbSet
            .Where(f => f.PacienteId == pacienteId)
            .Include(f => f.Medicamentos)
            .OrderByDescending(f => f.FechaAtencion)
            .ToListAsync();
    }

    public async Task<FichaMedica?> GetWithMedicamentosAsync(int fichaId)
    {
        return await DbSet
            .Include(f => f.Medicamentos)
            .Include(f => f.Paciente)
            .FirstOrDefaultAsync(f => f.Id == fichaId);
    }

    public async Task<IEnumerable<FichaMedica>> GetByFechaRangoAsync(DateTime fechaInicio, DateTime fechaFin)
    {
        return await DbSet
            .Where(f => f.FechaAtencion >= fechaInicio && f.FechaAtencion <= fechaFin)
            .Include(f => f.Paciente)
            .OrderByDescending(f => f.FechaAtencion)
            .ToListAsync();
    }
}
