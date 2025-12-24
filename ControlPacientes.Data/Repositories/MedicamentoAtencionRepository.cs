using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Context;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Repositorio específico para Medicamentos de Atención
/// </summary>
public class MedicamentoAtencionRepository : GenericRepository<MedicamentoAtencion>, IMedicamentoAtencionRepository
{
    public MedicamentoAtencionRepository(ApplicationDbContext context) : base(context)
    {
    }

    public async Task<IEnumerable<MedicamentoAtencion>> GetByFichaMedicaAsync(int fichaMedicaId)
    {
        return await DbSet
            .Where(m => m.FichaMedicaId == fichaMedicaId)
            .OrderBy(m => m.NombreMedicamento)
            .ToListAsync();
    }

    public async Task DeleteByFichaMedicaAsync(int fichaMedicaId)
    {
        var medicamentos = await DbSet
            .Where(m => m.FichaMedicaId == fichaMedicaId)
            .ToListAsync();
        
        DbSet.RemoveRange(medicamentos);
        await Context.SaveChangesAsync();
    }
}
