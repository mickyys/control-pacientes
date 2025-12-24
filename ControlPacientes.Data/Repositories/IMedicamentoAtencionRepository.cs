using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Interfaz para repositorio de Medicamentos de Atención
/// </summary>
public interface IMedicamentoAtencionRepository : IGenericRepository<MedicamentoAtencion>
{
    Task<IEnumerable<MedicamentoAtencion>> GetByFichaMedicaAsync(int fichaMedicaId);
    
    Task DeleteByFichaMedicaAsync(int fichaMedicaId);
}
