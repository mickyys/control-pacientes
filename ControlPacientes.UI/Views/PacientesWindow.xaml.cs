using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows;
using ControlPacientes.Business.Services;
using ControlPacientes.Core.Models;

namespace ControlPacientes.UI.Views
{
    public partial class PacientesWindow : Window
    {
        // El servicio puede ser nulo en tiempo de diseño; por eso es nullable.
        private readonly IPacienteService? _pacienteService;
        private ObservableCollection<Paciente> _pacientes = new();

        // Constructor parameterless para el diseñador XAML / creación sin DI
        public PacientesWindow()
        {
            InitializeComponent();
            PacientesGrid.ItemsSource = _pacientes;
            // No llamar a operaciones que requieran el servicio cuando éste no existe
        }

        // Constructor para inyección de dependencias en tiempo de ejecución
        public PacientesWindow(IPacienteService pacienteService)
            : this()
        {
            _pacienteService = pacienteService ?? throw new ArgumentNullException(nameof(pacienteService));

            // Cargas iniciales (seguras porque _pacienteService no es null aquí)
            CargarCiudades();
            CargarPacientes();
        }

        private async void CargarPacientes(string searchTerm = "", string ciudad = "")
        {
            if (_pacienteService == null)
                return; // Evitar NullReference durante diseño o si no se inyectó el servicio

            try
            {
                IEnumerable<Paciente> pacientes;

                if (string.IsNullOrEmpty(searchTerm) && string.IsNullOrEmpty(ciudad))
                    pacientes = await _pacienteService.ObtenerTodosPacientesAsync();
                else
                    pacientes = await _pacienteService.BuscarPacientesAsync(searchTerm, ciudad);

                _pacientes.Clear();
                foreach (var paciente in pacientes)
                    _pacientes.Add(paciente);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al cargar pacientes: {ex.Message}", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private async void CargarCiudades()
        {
            if (_pacienteService == null)
                return; // Evitar NullReference durante diseño o si no se inyectó el servicio

            try
            {
                var ciudades = await _pacienteService.ObtenerCiudadesAsync();
                CiudadCombo.ItemsSource = ciudades.Prepend("Todas las ciudades").ToList();
                CiudadCombo.SelectedIndex = 0;
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al cargar ciudades: {ex.Message}");
            }
        }

        private void SearchBox_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {
            var searchTerm = SearchBox.Text;
            var ciudad = CiudadCombo.SelectedItem?.ToString();

            if (ciudad == "Todas las ciudades")
                ciudad = "";

            CargarPacientes(searchTerm, ciudad);
        }

        private void CiudadCombo_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
        {
            var searchTerm = SearchBox.Text;
            var ciudad = CiudadCombo.SelectedItem?.ToString();

            if (ciudad == "Todas las ciudades")
                ciudad = "";

            CargarPacientes(searchTerm, ciudad);
        }

        private void NuevoPaciente_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new EditarPacienteWindow();
            ventana.ShowDialog();
            CargarPacientes();
        }

        private void PacienteSeleccionado_Click(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            if (PacientesGrid.SelectedItem is Paciente paciente)
            {
                var ventana = new EditarPacienteWindow(paciente);
                ventana.ShowDialog();
                CargarPacientes();
            }
        }

        private void Editar_Click(object sender, RoutedEventArgs e)
        {
            if (PacientesGrid.SelectedItem is Paciente paciente)
            {
                var ventana = new EditarPacienteWindow(paciente);
                ventana.ShowDialog();
                CargarPacientes();
            }
            else
                MessageBox.Show("Selecciona un paciente para editar");
        }

        private async void Eliminar_Click(object sender, RoutedEventArgs e)
        {
            if (PacientesGrid.SelectedItem is Paciente paciente)
            {
                var resultado = MessageBox.Show($"¿Eliminar a {paciente.NombreCompleto}?",
                    "Confirmar eliminación", MessageBoxButton.YesNo, MessageBoxImage.Warning);

                if (resultado == MessageBoxResult.Yes)
                {
                    try
                    {
                        if (_pacienteService == null)
                            throw new InvalidOperationException("Servicio de pacientes no disponible");

                        await _pacienteService.EliminarPacienteAsync(paciente.Id);
                        CargarPacientes();
                        MessageBox.Show("Paciente eliminado correctamente");
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show($"Error al eliminar: {ex.Message}", "Error");
                    }
                }
            }
            else
                MessageBox.Show("Selecciona un paciente para eliminar");
        }

        private void Limpiar_Click(object sender, RoutedEventArgs e)
        {
            SearchBox.Clear();
            CiudadCombo.SelectedIndex = 0;
            CargarPacientes();
        }

        private void Actualizar_Click(object sender, RoutedEventArgs e)
        {
            CargarCiudades();
            CargarPacientes();
        }

        private void Cerrar_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }
    }
}
