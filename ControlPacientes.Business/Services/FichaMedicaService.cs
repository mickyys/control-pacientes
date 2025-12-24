using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Repositories;
using ControlPacientes.Business.Exceptions;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Servicio para la lógica de negocio de Fichas Médicas
/// </summary>
public class FichaMedicaService : IFichaMedicaService
{
    private readonly IFichaMedicaRepository _repository;
    private readonly IPacienteRepository _pacienteRepository;
    private readonly IMedicamentoAtencionRepository _medicamentoRepository;

    public FichaMedicaService(
        IFichaMedicaRepository repository,
        IPacienteRepository pacienteRepository,
        IMedicamentoAtencionRepository medicamentoRepository)
    {
        _repository = repository ?? throw new ArgumentNullException(nameof(repository));
        _pacienteRepository = pacienteRepository ?? throw new ArgumentNullException(nameof(pacienteRepository));
        _medicamentoRepository = medicamentoRepository ?? throw new ArgumentNullException(nameof(medicamentoRepository));
    }

    public async Task<FichaMedica> CrearFichaMedicaAsync(FichaMedica ficha)
    {
        await ValidarFichaMedica(ficha);

        ficha.FechaCreacion = DateTime.Now;

        return await _repository.AddAsync(ficha);
    }

    public async Task<FichaMedica> ActualizarFichaMedicaAsync(FichaMedica ficha)
    {
        await ValidarFichaMedica(ficha);

        var existente = await _repository.GetByIdAsync(ficha.Id);
        if (existente == null)
            throw new EntityNotFoundException($"Ficha médica con ID {ficha.Id} no encontrada");

        ficha.FechaCreacion = existente.FechaCreacion;
        ficha.FechaActualizacion = DateTime.Now;

        return await _repository.UpdateAsync(ficha);
    }

    public async Task<FichaMedica?> ObtenerFichaMedicaAsync(int id)
    {
        return await _repository.GetWithMedicamentosAsync(id);
    }

    public async Task<IEnumerable<FichaMedica>> ObtenerFichasPorPacienteAsync(int pacienteId)
    {
        return await _repository.GetByPacienteAsync(pacienteId);
    }

    public async Task<IEnumerable<FichaMedica>> ObtenerFichasEnRangoFechaAsync(DateTime fechaInicio, DateTime fechaFin)
    {
        return await _repository.GetByFechaRangoAsync(fechaInicio, fechaFin);
    }

    public async Task EliminarFichaMedicaAsync(int id)
    {
        // Primero eliminar medicamentos asociados
        await _medicamentoRepository.DeleteByFichaMedicaAsync(id);

        // Luego eliminar la ficha
        var ficha = await _repository.GetByIdAsync(id);
        if (ficha == null)
            throw new EntityNotFoundException($"Ficha médica con ID {id} no encontrada");

        await _repository.DeleteAsync(ficha);
    }

    private async Task ValidarFichaMedica(FichaMedica ficha)
    {
        if (ficha == null)
            throw new ValidationException("La ficha médica no puede ser nula");

        // Validar que el paciente existe
        var paciente = await _pacienteRepository.GetByIdAsync(ficha.PacienteId);
        if (paciente == null)
            throw new EntityNotFoundException($"Paciente con ID {ficha.PacienteId} no encontrado");

        if (string.IsNullOrWhiteSpace(ficha.ProfesionalNombre))
            throw new ValidationException("El nombre del profesional es requerido");

        if (ficha.FechaAtencion > DateTime.Now)
            throw new ValidationException("La fecha de atención no puede ser futura");

        if (ficha.FechaAtencion.Year < 2000)
            throw new ValidationException("La fecha de atención no es válida");
    }
}
