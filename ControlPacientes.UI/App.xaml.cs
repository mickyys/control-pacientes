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

            // Vistas: registrar como transientes para ventanas
            services.AddTransient<MainWindow>();
            services.AddTransient<PacientesWindow>();
            services.AddTransient<EditarPacienteWindow>();
            services.AddTransient<FichasMedicasWindow>();
            services.AddTransient<EditarFichaMedicaWindow>();
        }

        protected override async void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            try
            {
                await DataServiceCollectionExtensions.InitializeDatabaseAsync(_serviceProvider);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error inicializando base de datos: {ex.Message}", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                Shutdown(-1);
                return;
            }

            var mainWindow = _serviceProvider.GetRequiredService<MainWindow>();
            mainWindow.Show();
        }
    }
}
