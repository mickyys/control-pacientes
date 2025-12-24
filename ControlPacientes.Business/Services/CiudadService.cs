using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using ControlPacientes.Core.Models;
using ControlPacientes.Data.Repositories;

namespace ControlPacientes.Business.Services;

/// <summary>
/// Servicio para la lógica de negocio de Ciudades
/// </summary>
public class CiudadService : ICiudadService
{
    private readonly ICiudadRepository _repository;

    public CiudadService(ICiudadRepository repository)
    {
        _repository = repository ?? throw new ArgumentNullException(nameof(repository));
    }

    public async Task<IEnumerable<Ciudad>> ObtenerTodasAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<IEnumerable<Ciudad>> BuscarCiudadesAsync(string searchTerm)
    {
        return await _repository.SearchAsync(searchTerm);
    }

    public async Task<Ciudad> AsegurarCiudadExisteAsync(string nombre)
    {
        if (string.IsNullOrWhiteSpace(nombre))
            throw new ArgumentException("El nombre de la ciudad no puede estar vacío", nameof(nombre));

        var nombreNormalizado = nombre.Trim();
        var existente = await _repository.GetByNombreAsync(nombreNormalizado);
        
        if (existente != null)
            return existente;

        var nuevaCiudad = new Ciudad { Nombre = nombreNormalizado };
        return await _repository.AddAsync(nuevaCiudad);
    }
}
