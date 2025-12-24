using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Interfaz para repositorio de Fichas Médicas
/// </summary>
public interface IFichaMedicaRepository : IGenericRepository<FichaMedica>
{
    Task<IEnumerable<FichaMedica>> GetByPacienteAsync(int pacienteId);
    
    Task<FichaMedica?> GetWithMedicamentosAsync(int fichaId);
    
    Task<IEnumerable<FichaMedica>> GetByFechaRangoAsync(DateTime fechaInicio, DateTime fechaFin);
}
