using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Repositories;
using ControlPacientes.Business.Exceptions;
using ControlPacientes.Business.Validators;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Servicio para la lógica de negocio de Pacientes
/// </summary>
public class PacienteService : IPacienteService
{
    private readonly IPacienteRepository _repository;

    public PacienteService(IPacienteRepository repository)
    {
        _repository = repository ?? throw new ArgumentNullException(nameof(repository));
    }

    public async Task<Paciente> CrearPacienteAsync(Paciente paciente)
    {
        ValidarPaciente(paciente);

        // Validar que el RUT no exista
        var existente = await _repository.GetByRutAsync(paciente.Rut);
        if (existente != null)
            throw new DuplicateEntityException($"Ya existe un paciente registrado con el RUT: {paciente.Rut}");

        // Formatear el RUT
        paciente.Rut = RutValidator.FormatRut(paciente.Rut);
        paciente.FechaRegistro = DateTime.Now;

        return await _repository.AddAsync(paciente);
    }

    public async Task<Paciente> ActualizarPacienteAsync(Paciente paciente)
    {
        ValidarPaciente(paciente);

        var existente = await _repository.GetByIdAsync(paciente.Id);
        if (existente == null)
            throw new EntityNotFoundException($"Paciente con ID {paciente.Id} no encontrado");

        // Si cambió el RUT, validar que no exista otro con ese RUT
        if (existente.Rut != paciente.Rut)
        {
            var duplicado = await _repository.GetByRutAsync(paciente.Rut);
            if (duplicado != null)
                throw new DuplicateEntityException($"Ya existe un paciente con el RUT: {paciente.Rut}");
        }

        paciente.Rut = RutValidator.FormatRut(paciente.Rut);
        paciente.FechaRegistro = existente.FechaRegistro;
        paciente.FechaActualizacion = DateTime.Now;

        return await _repository.UpdateAsync(paciente);
    }

    public async Task<Paciente?> ObtenerPacienteAsync(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Paciente?> ObtenerPacienteConFichasAsync(int id)
    {
        return await _repository.GetWithFichasAsync(id);
    }

    public async Task<Paciente?> BuscarPorRutAsync(string rut)
    {
        if (string.IsNullOrWhiteSpace(rut))
            throw new ValidationException("El RUT no puede estar vacío");

        rut = RutValidator.FormatRut(rut);
        return await _repository.GetByRutAsync(rut);
    }

    public async Task<IEnumerable<Paciente>> BuscarPacientesAsync(string? searchTerm, string? ciudad = null)
    {
        if (string.IsNullOrWhiteSpace(searchTerm) && string.IsNullOrWhiteSpace(ciudad))
            return await _repository.GetAllAsync();

        return await _repository.SearchAsync(searchTerm ?? string.Empty, ciudad);
    }

    public async Task<IEnumerable<Paciente>> ObtenerTodosPacientesAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task EliminarPacienteAsync(int id)
    {
        var paciente = await _repository.GetByIdAsync(id);
        if (paciente == null)
            throw new EntityNotFoundException($"Paciente con ID {id} no encontrado");

        await _repository.DeleteAsync(paciente);
    }

    public async Task<IEnumerable<string>> ObtenerCiudadesAsync()
    {
        return await _repository.GetCiudadesAsync();
    }

    public async Task<int> ContarPacientesAsync()
    {
        return await _repository.CountAsync();
    }

    private void ValidarPaciente(Paciente paciente)
    {
        if (paciente == null)
            throw new ValidationException("El paciente no puede ser nulo");

        if (string.IsNullOrWhiteSpace(paciente.Nombre))
            throw new ValidationException("El nombre del paciente es requerido");

        if (string.IsNullOrWhiteSpace(paciente.Apellido))
            throw new ValidationException("El apellido del paciente es requerido");

        if (string.IsNullOrWhiteSpace(paciente.Rut))
            throw new ValidationException("El RUT del paciente es requerido");

        if (!RutValidator.IsValidRut(paciente.Rut))
            throw new ValidationException("El RUT no tiene un formato válido");

        if (paciente.FechaNacimiento > DateTime.Now)
            throw new ValidationException("La fecha de nacimiento no puede ser futura");

        if (!string.IsNullOrWhiteSpace(paciente.Email))
        {
            if (!paciente.Email.Contains("@"))
                throw new ValidationException("El email no tiene un formato válido");
        }
    }
}
