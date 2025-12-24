using System;
using System.Windows;
using Microsoft.Extensions.DependencyInjection;
using ControlPacientes.Business.Extensions;
using ControlPacientes.Data.Extensions;
using ControlPacientes.UI.Views;

namespace ControlPacientes.UI
{
    public partial class App : Application
    {
        private readonly IServiceProvider _serviceProvider;

        public App()
        {
            var services = new ServiceCollection();
            
            // Configurar servicios
            ConfigureServices(services);
            
            _serviceProvider = services.BuildServiceProvider();
        }

        private void ConfigureServices(IServiceCollection services)
        {
            // Base de datos
            string connectionString = "Data Source=ControlPacientes.db";
            services.AddDataServices(connectionString);

            // Servicios de negocio
            services.AddBusinessServices();

            // Vistas
            services.AddScoped<MainWindow>();
            services.AddScoped<PacientesWindow>();
            services.AddScoped<EditarPacienteWindow>();
            services.AddScoped<FichasMedicasWindow>();
            services.AddScoped<EditarFichaMedicaWindow>();
        }

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            // Inicializar base de datos
            var task = DataServiceCollectionExtensions.InitializeDatabaseAsync(_serviceProvider);
            task.Wait();

            // Mostrar ventana principal
            var mainWindow = _serviceProvider.GetRequiredService<MainWindow>();
            mainWindow.Show();
        }
    }
}
