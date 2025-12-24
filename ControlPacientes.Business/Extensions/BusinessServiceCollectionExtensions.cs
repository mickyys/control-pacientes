using ControlPacientes.Business.Services;
using ControlPacientes.Data.Extensions;
using ControlPacientes.Data.Repositories;
using Microsoft.Extensions.DependencyInjection;

namespace ControlPacientes.Business.Extensions;

/// <summary>
/// Extensiones para inyección de dependencias de lógica de negocio
/// </summary>
public static class BusinessServiceCollectionExtensions
{
    public static IServiceCollection AddBusinessServices(this IServiceCollection services)
    {
        // Registrar servicios
        services.AddScoped<IPacienteService, PacienteService>();
        services.AddScoped<IFichaMedicaService, FichaMedicaService>();
        services.AddScoped<IMedicamentoAtencionService, MedicamentoAtencionService>();

        return services;
    }
}
