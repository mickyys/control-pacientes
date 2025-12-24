using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Repositories;
using ControlPacientes.Business.Exceptions;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Servicio para la lógica de negocio de Medicamentos de Atención
/// </summary>
public class MedicamentoAtencionService : IMedicamentoAtencionService
{
    private readonly IMedicamentoAtencionRepository _repository;
    private readonly IFichaMedicaRepository _fichaMedicaRepository;

    public MedicamentoAtencionService(
        IMedicamentoAtencionRepository repository,
        IFichaMedicaRepository fichaMedicaRepository)
    {
        _repository = repository ?? throw new ArgumentNullException(nameof(repository));
        _fichaMedicaRepository = fichaMedicaRepository ?? throw new ArgumentNullException(nameof(fichaMedicaRepository));
    }

    public async Task<MedicamentoAtencion> AgregarMedicamentoAsync(MedicamentoAtencion medicamento)
    {
        await ValidarMedicamento(medicamento);

        medicamento.FechaCreacion = DateTime.Now;

        return await _repository.AddAsync(medicamento);
    }

    public async Task<MedicamentoAtencion> ActualizarMedicamentoAsync(MedicamentoAtencion medicamento)
    {
        await ValidarMedicamento(medicamento);

        var existente = await _repository.GetByIdAsync(medicamento.Id);
        if (existente == null)
            throw new EntityNotFoundException($"Medicamento con ID {medicamento.Id} no encontrado");

        medicamento.FechaCreacion = existente.FechaCreacion;

        return await _repository.UpdateAsync(medicamento);
    }

    public async Task<MedicamentoAtencion?> ObtenerMedicamentoAsync(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<IEnumerable<MedicamentoAtencion>> ObtenerMedicamentosPorFichaAsync(int fichaMedicaId)
    {
        return await _repository.GetByFichaMedicaAsync(fichaMedicaId);
    }

    public async Task EliminarMedicamentoAsync(int id)
    {
        var medicamento = await _repository.GetByIdAsync(id);
        if (medicamento == null)
            throw new EntityNotFoundException($"Medicamento con ID {id} no encontrado");

        await _repository.DeleteAsync(medicamento);
    }

    public async Task EliminarMedicamentosPorFichaAsync(int fichaMedicaId)
    {
        await _repository.DeleteByFichaMedicaAsync(fichaMedicaId);
    }

    private async Task ValidarMedicamento(MedicamentoAtencion medicamento)
    {
        if (medicamento == null)
            throw new ValidationException("El medicamento no puede ser nulo");

        // Validar que la ficha existe
        var ficha = await _fichaMedicaRepository.GetByIdAsync(medicamento.FichaMedicaId);
        if (ficha == null)
            throw new EntityNotFoundException($"Ficha médica con ID {medicamento.FichaMedicaId} no encontrada");

        if (string.IsNullOrWhiteSpace(medicamento.NombreMedicamento))
            throw new ValidationException("El nombre del medicamento es requerido");

        if (string.IsNullOrWhiteSpace(medicamento.Dosis))
            throw new ValidationException("La dosis es requerida");

        if (string.IsNullOrWhiteSpace(medicamento.Frecuencia))
            throw new ValidationException("La frecuencia es requerida");

        if (string.IsNullOrWhiteSpace(medicamento.Duracion))
            throw new ValidationException("La duración es requerida");
    }
}
