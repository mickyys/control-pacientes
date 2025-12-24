using System;
using System.Windows;
using ControlPacientes.Business.Services;

namespace ControlPacientes.UI.Views
{
    public partial class MainWindow : Window
    {
        private readonly IPacienteService _pacienteService;

        public MainWindow(IPacienteService pacienteService)
        {
            InitializeComponent();
            _pacienteService = pacienteService;
            CargarInformacion();
        }

        private async void CargarInformacion()
        {
            try
            {
                int totalPacientes = await _pacienteService.ContarPacientesAsync();
                InfoText.Text = $"Total de pacientes registrados: {totalPacientes}\n" +
                               $"Base de datos: SQLite (Offline)\n" +
                               $"Estado: Listo para usar";
            }
            catch (Exception ex)
            {
                InfoText.Text = $"Error al cargar información: {ex.Message}";
            }
        }

        private void AbrirPacientes_Click(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            var ventana = new PacientesWindow();
            ventana.Show();
        }

        private void AbrirFichas_Click(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            MessageBox.Show("Módulo de Fichas Médicas - Próximamente");
        }
    }
}
