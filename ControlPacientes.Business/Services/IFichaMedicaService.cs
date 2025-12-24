using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Interfaz para servicio de Fichas Médicas
/// </summary>
public interface IFichaMedicaService
{
    Task<FichaMedica> CrearFichaMedicaAsync(FichaMedica ficha);
    
    Task<FichaMedica> ActualizarFichaMedicaAsync(FichaMedica ficha);
    
    Task<FichaMedica?> ObtenerFichaMedicaAsync(int id);
    
    Task<IEnumerable<FichaMedica>> ObtenerFichasPorPacienteAsync(int pacienteId);
    
    Task<IEnumerable<FichaMedica>> ObtenerFichasEnRangoFechaAsync(DateTime fechaInicio, DateTime fechaFin);
    
    Task EliminarFichaMedicaAsync(int id);
}
