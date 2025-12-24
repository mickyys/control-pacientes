using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Interfaz para servicio de Medicamentos de Atención
/// </summary>
public interface IMedicamentoAtencionService
{
    Task<MedicamentoAtencion> AgregarMedicamentoAsync(MedicamentoAtencion medicamento);
    
    Task<MedicamentoAtencion> ActualizarMedicamentoAsync(MedicamentoAtencion medicamento);
    
    Task<MedicamentoAtencion?> ObtenerMedicamentoAsync(int id);
    
    Task<IEnumerable<MedicamentoAtencion>> ObtenerMedicamentosPorFichaAsync(int fichaMedicaId);
    
    Task EliminarMedicamentoAsync(int id);
    
    Task EliminarMedicamentosPorFichaAsync(int fichaMedicaId);
}
