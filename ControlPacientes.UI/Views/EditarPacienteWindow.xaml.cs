using System;
using System.Windows;
using ControlPacientes.Business.Services;
using ControlPacientes.Core.Models;

namespace ControlPacientes.UI.Views
{
    public partial class EditarPacienteWindow : Window
    {
        private readonly IPacienteService? _pacienteService;
        private readonly ICiudadService? _ciudadService;
        private Paciente? _pacienteActual;

        public EditarPacienteWindow(IPacienteService pacienteService, ICiudadService ciudadService, Paciente? paciente = null)
        {
            InitializeComponent();
            _pacienteService = pacienteService;
            _ciudadService = ciudadService;
            _pacienteActual = paciente;
            
            CargarCiudades();

            if (paciente != null)
            {
                Title = "Editar Paciente";
                CargarDatos(paciente);
            }
            else
            {
                Title = "Nuevo Paciente";
                FechaNacimientoBox.SelectedDate = DateTime.Now.AddYears(-30);
            }
        }

        private async void CargarCiudades()
        {
            if (_ciudadService == null) return;
            try
            {
                var ciudades = await _ciudadService.ObtenerTodasAsync();
                CiudadCombo.ItemsSource = ciudades;
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al cargar ciudades: {ex.Message}");
            }
        }

        private void CargarDatos(Paciente paciente)
        {
            RutBox.Text = paciente.Rut;
            NombreBox.Text = paciente.Nombre;
            ApellidoBox.Text = paciente.Apellido;
            FechaNacimientoBox.SelectedDate = paciente.FechaNacimiento;
            EmailBox.Text = paciente.Email;
            TelefonoBox.Text = paciente.Telefono;
            CiudadCombo.Text = paciente.Ciudad;
            DireccionBox.Text = paciente.Direccion;
            NotasBox.Text = paciente.NotasClinicas;
        }

        private async void Guardar_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                if (_pacienteService == null || _ciudadService == null)
                    throw new InvalidOperationException("Servicios no disponibles");

                string ciudadNombre = CiudadCombo.Text.Trim();
                if (string.IsNullOrWhiteSpace(ciudadNombre))
                {
                    MessageBox.Show("La ciudad es obligatoria", "Validación", MessageBoxButton.OK, MessageBoxImage.Warning);
                    return;
                }

                // Asegurar que la ciudad existe en la tabla de ciudades
                await _ciudadService.AsegurarCiudadExisteAsync(ciudadNombre);

                var paciente = new Paciente
                {
                    Id = _pacienteActual?.Id ?? 0,
                    Rut = RutBox.Text.Trim(),
                    Nombre = NombreBox.Text.Trim(),
                    Apellido = ApellidoBox.Text.Trim(),
                    FechaNacimiento = FechaNacimientoBox.SelectedDate ?? DateTime.Now,
                    Email = EmailBox.Text.Trim(),
                    Telefono = TelefonoBox.Text.Trim(),
                    Ciudad = ciudadNombre,
                    Direccion = DireccionBox.Text.Trim(),
                    NotasClinicas = NotasBox.Text.Trim()
                };

                if (_pacienteActual == null)
                {
                    await _pacienteService.CrearPacienteAsync(paciente);
                }
                else
                {
                    await _pacienteService.ActualizarPacienteAsync(paciente);
                }

                MessageBox.Show("Paciente guardado correctamente", "Éxito", 
                    MessageBoxButton.OK, MessageBoxImage.Information);
                Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error: {ex.Message}", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void Cancelar_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }
    }
}
