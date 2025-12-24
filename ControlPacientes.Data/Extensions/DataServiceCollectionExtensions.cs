using System;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using ControlPacientes.Data.Context;
using ControlPacientes.Data.Repositories;
using Microsoft.Extensions.DependencyInjection;

namespace ControlPacientes.Data.Extensions;

/// <summary>
/// Extensiones para inyección de dependencias de acceso a datos
/// </summary>
public static class DataServiceCollectionExtensions
{
    public static IServiceCollection AddDataServices(this IServiceCollection services, string connectionString)
    {
        // Registrar DbContext
        services.AddDbContext<ApplicationDbContext>(options =>
            options.UseSqlite(connectionString));

        // Registrar repositorios
        services.AddScoped<IPacienteRepository, PacienteRepository>();
        services.AddScoped<IFichaMedicaRepository, FichaMedicaRepository>();
        services.AddScoped<IMedicamentoAtencionRepository, MedicamentoAtencionRepository>();

        return services;
    }

    public static async Task InitializeDatabaseAsync(IServiceProvider serviceProvider)
    {
        using (var scope = serviceProvider.CreateScope())
        {
            var context = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            await context.Database.MigrateAsync();
        }
    }
}
