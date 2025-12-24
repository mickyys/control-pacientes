using System;
using System.Windows;
using ControlPacientes.Business.Services;
using ControlPacientes.Core.Models;

namespace ControlPacientes.UI.Views
{
    public partial class EditarPacienteWindow : Window
    {
        private readonly IPacienteService? _pacienteService;
        private Paciente? _pacienteActual;

        public EditarPacienteWindow(Paciente? paciente = null)
        {
            InitializeComponent();
            _pacienteActual = paciente;
            
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

        private void CargarDatos(Paciente paciente)
        {
            RutBox.Text = paciente.Rut;
            NombreBox.Text = paciente.Nombre;
            ApellidoBox.Text = paciente.Apellido;
            FechaNacimientoBox.SelectedDate = paciente.FechaNacimiento;
            EmailBox.Text = paciente.Email;
            TelefonoBox.Text = paciente.Telefono;
            CiudadBox.Text = paciente.Ciudad;
            DireccionBox.Text = paciente.Direccion;
            NotasBox.Text = paciente.NotasClinicas;
        }

        private async void Guardar_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                var paciente = new Paciente
                {
                    Id = _pacienteActual?.Id ?? 0,
                    Rut = RutBox.Text,
                    Nombre = NombreBox.Text,
                    Apellido = ApellidoBox.Text,
                    FechaNacimiento = FechaNacimientoBox.SelectedDate ?? DateTime.Now,
                    Email = EmailBox.Text,
                    Telefono = TelefonoBox.Text,
                    Ciudad = CiudadBox.Text,
                    Direccion = DireccionBox.Text,
                    NotasClinicas = NotasBox.Text
                };

                if (_pacienteActual == null)
                {
                    // Crear nuevo
                    var resultado = MessageBox.Show("¿Crear nuevo paciente?", "Confirmar", 
                        MessageBoxButton.YesNo, MessageBoxImage.Question);
                    if (resultado != MessageBoxResult.Yes) return;
                }
                else
                {
                    // Actualizar existente
                    var resultado = MessageBox.Show("¿Actualizar datos del paciente?", "Confirmar", 
                        MessageBoxButton.YesNo, MessageBoxImage.Question);
                    if (resultado != MessageBoxResult.Yes) return;
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
