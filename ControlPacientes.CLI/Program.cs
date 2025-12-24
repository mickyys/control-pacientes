using System;
using System.Collections.ObjectModel;
using Microsoft.Extensions.DependencyInjection;
using ControlPacientes.Business.Extensions;
using ControlPacientes.Data.Extensions;
using ControlPacientes.Business.Services;
using ControlPacientes.Core.Models;

namespace ControlPacientes.CLI
{
    class Program
    {
        private static IPacienteService? _pacienteService;
        private static IFichaMedicaService? _fichaMedicaService;

        static async Task Main(string[] args)
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8;
            
            // Configurar servicios
            var services = new ServiceCollection();
            string connectionString = "Data Source=ControlPacientes.db";
            services.AddDataServices(connectionString);
            services.AddBusinessServices();
            
            var serviceProvider = services.BuildServiceProvider();
            _pacienteService = serviceProvider.GetRequiredService<IPacienteService>();
            _fichaMedicaService = serviceProvider.GetRequiredService<IFichaMedicaService>();

            await MostrarMenuPrincipal();
        }

        static async Task MostrarMenuPrincipal()
        {
            bool salir = false;
            while (!salir)
            {
                Console.Clear();
                Console.WriteLine("╔════════════════════════════════════════╗");
                Console.WriteLine("║     CONTROL DE PACIENTES               ║");
                Console.WriteLine("║   Sistema de Gestión Médica Offline    ║");
                Console.WriteLine("╚════════════════════════════════════════╝");
                Console.WriteLine();
                Console.WriteLine("1. 👥 Gestionar Pacientes");
                Console.WriteLine("2. 📋 Fichas Médicas");
                Console.WriteLine("3. 📊 Reportes");
                Console.WriteLine("4. ❌ Salir");
                Console.WriteLine();
                Console.Write("Seleccione una opción: ");
                
                string? opcion = Console.ReadLine();
                
                switch (opcion)
                {
                    case "1":
                        await MostrarMenuPacientes();
                        break;
                    case "2":
                        await MostrarMenuFichas();
                        break;
                    case "3":
                        await MostrarReportes();
                        break;
                    case "4":
                        salir = true;
                        Console.WriteLine("\n¡Hasta luego!");
                        break;
                    default:
                        Console.WriteLine("\nOpción no válida. Presione Enter para continuar...");
                        Console.ReadLine();
                        break;
                }
            }
        }

        static async Task MostrarMenuPacientes()
        {
            bool volver = false;
            while (!volver)
            {
                Console.Clear();
                Console.WriteLine("═══════════════════════════════════════");
                Console.WriteLine("   GESTIÓN DE PACIENTES");
                Console.WriteLine("═══════════════════════════════════════\n");
                
                Console.WriteLine("1. 📝 Nuevo Paciente");
                Console.WriteLine("2. 🔍 Buscar Paciente");
                Console.WriteLine("3. 📄 Listar Todos");
                Console.WriteLine("4. ✏️  Editar Paciente");
                Console.WriteLine("5. 🗑️  Eliminar Paciente");
                Console.WriteLine("6. ← Volver");
                Console.WriteLine();
                Console.Write("Seleccione una opción: ");
                
                string? opcion = Console.ReadLine();
                
                switch (opcion)
                {
                    case "1":
                        await CrearPaciente();
                        break;
                    case "2":
                        await BuscarPaciente();
                        break;
                    case "3":
                        await ListarPacientes();
                        break;
                    case "4":
                        await EditarPaciente();
                        break;
                    case "5":
                        await EliminarPaciente();
                        break;
                    case "6":
                        volver = true;
                        break;
                    default:
                        Console.WriteLine("\nOpción no válida. Presione Enter...");
                        Console.ReadLine();
                        break;
                }
            }
        }

        static async Task CrearPaciente()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   CREAR NUEVO PACIENTE");
            Console.WriteLine("═══════════════════════════════════════\n");

            Console.Write("RUT: ");
            var rut = Console.ReadLine();
            
            Console.Write("Nombre: ");
            var nombre = Console.ReadLine();
            
            Console.Write("Apellido: ");
            var apellido = Console.ReadLine();
            
            Console.Write("Ciudad: ");
            var ciudad = Console.ReadLine();
            
            Console.Write("Teléfono: ");
            var telefono = Console.ReadLine();
            
            Console.Write("Email: ");
            var email = Console.ReadLine();
            
            Console.Write("Dirección: ");
            var direccion = Console.ReadLine();

            try
            {
                var paciente = new Paciente
                {
                    Rut = rut,
                    Nombre = nombre,
                    Apellido = apellido,
                    Ciudad = ciudad,
                    Telefono = telefono,
                    Email = email,
                    Direccion = direccion
                };

                await _pacienteService!.CrearPacienteAsync(paciente);
                Console.WriteLine("\n✅ Paciente creado exitosamente.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n❌ Error: {ex.Message}");
            }

            Console.WriteLine("\nPresione Enter para continuar...");
            Console.ReadLine();
        }

        static async Task ListarPacientes()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   LISTADO DE PACIENTES");
            Console.WriteLine("═══════════════════════════════════════\n");

            try
            {
                var pacientes = await _pacienteService!.ObtenerTodosPacientesAsync();
                var pacientesList = pacientes?.ToList();
                
                if (pacientesList == null || pacientesList.Count == 0)
                {
                    Console.WriteLine("No hay pacientes registrados.");
                }
                else
                {
                    Console.WriteLine($"{"RUT",-15} {"Nombre",-15} {"Apellido",-15} {"Ciudad",-15} {"Teléfono",-12}");
                    Console.WriteLine(new string('─', 72));
                    
                    foreach (var p in pacientesList)
                    {
                        var rut = (p.Rut ?? "").PadRight(15).Substring(0, 15);
                        var nombre = (p.Nombre ?? "").PadRight(15).Substring(0, 15);
                        var apellido = (p.Apellido ?? "").PadRight(15).Substring(0, 15);
                        var ciudad = (p.Ciudad ?? "").PadRight(15).Substring(0, 15);
                        var telefono = (p.Telefono ?? "").PadRight(12).Substring(0, 12);
                        Console.WriteLine($"{rut} {nombre} {apellido} {ciudad} {telefono}");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }

            Console.WriteLine("\nPresione Enter para continuar...");
            Console.ReadLine();
        }

        static async Task BuscarPaciente()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   BUSCAR PACIENTE");
            Console.WriteLine("═══════════════════════════════════════\n");

            Console.Write("Ingrese nombre, apellido o RUT: ");
            var termino = Console.ReadLine();

            try
            {
                var pacientes = await _pacienteService!.ObtenerTodosPacientesAsync();
                var resultados = pacientes?
                    .Where(p => (p.Nombre?.Contains(termino ?? "", StringComparison.OrdinalIgnoreCase) ?? false) ||
                               (p.Apellido?.Contains(termino ?? "", StringComparison.OrdinalIgnoreCase) ?? false) ||
                               (p.Rut?.Contains(termino ?? "", StringComparison.OrdinalIgnoreCase) ?? false))
                    .ToList();

                if (resultados == null || resultados.Count == 0)
                {
                    Console.WriteLine("\nNo se encontraron resultados.");
                }
                else
                {
                    Console.WriteLine($"{"RUT",-15} {"Nombre",-15} {"Apellido",-15} {"Ciudad",-15}");
                    Console.WriteLine(new string('─', 60));
                    
                    foreach (var p in resultados)
                    {
                        var rut = (p.Rut ?? "").PadRight(15).Substring(0, 15);
                        var nombre = (p.Nombre ?? "").PadRight(15).Substring(0, 15);
                        var apellido = (p.Apellido ?? "").PadRight(15).Substring(0, 15);
                        var ciudad = (p.Ciudad ?? "").PadRight(15).Substring(0, 15);
                        Console.WriteLine($"{rut} {nombre} {apellido} {ciudad}");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }

            Console.WriteLine("\nPresione Enter para continuar...");
            Console.ReadLine();
        }

        static async Task EditarPaciente()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   EDITAR PACIENTE");
            Console.WriteLine("═══════════════════════════════════════\n");

            Console.Write("Ingrese el RUT del paciente: ");
            var rut = Console.ReadLine();

            try
            {
                var pacientes = await _pacienteService!.ObtenerTodosPacientesAsync();
                var paciente = pacientes?.FirstOrDefault(p => p.Rut == rut);

                if (paciente == null)
                {
                    Console.WriteLine("\nPaciente no encontrado.");
                }
                else
                {
                    Console.WriteLine($"\nDatos actuales:");
                    Console.WriteLine($"Nombre: {paciente.Nombre}");
                    Console.WriteLine($"Apellido: {paciente.Apellido}");
                    Console.WriteLine($"Ciudad: {paciente.Ciudad}");

                    Console.Write("\nNuevo nombre (Enter para no cambiar): ");
                    var nombre = Console.ReadLine();
                    if (!string.IsNullOrEmpty(nombre)) paciente.Nombre = nombre;

                    Console.Write("Nuevo apellido (Enter para no cambiar): ");
                    var apellido = Console.ReadLine();
                    if (!string.IsNullOrEmpty(apellido)) paciente.Apellido = apellido;

                    Console.Write("Nueva ciudad (Enter para no cambiar): ");
                    var ciudad = Console.ReadLine();
                    if (!string.IsNullOrEmpty(ciudad)) paciente.Ciudad = ciudad;

                    Console.Write("Nuevo teléfono (Enter para no cambiar): ");
                    var telefono = Console.ReadLine();
                    if (!string.IsNullOrEmpty(telefono)) paciente.Telefono = telefono;

                    await _pacienteService!.ActualizarPacienteAsync(paciente);
                    Console.WriteLine("\n✅ Paciente actualizado exitosamente.");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n❌ Error: {ex.Message}");
            }

            Console.WriteLine("\nPresione Enter para continuar...");
            Console.ReadLine();
        }

        static async Task EliminarPaciente()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   ELIMINAR PACIENTE");
            Console.WriteLine("═══════════════════════════════════════\n");

            Console.Write("Ingrese el RUT del paciente: ");
            var rut = Console.ReadLine();

            try
            {
                var pacientes = await _pacienteService!.ObtenerTodosPacientesAsync();
                var paciente = pacientes?.FirstOrDefault(p => p.Rut == rut);

                if (paciente == null)
                {
                    Console.WriteLine("\nPaciente no encontrado.");
                }
                else
                {
                    Console.WriteLine($"\n¿Está seguro que desea eliminar a {paciente.Nombre} {paciente.Apellido}? (S/N)");
                    var confirmacion = Console.ReadLine()?.ToUpper();

                    if (confirmacion == "S")
                    {
                        await _pacienteService!.EliminarPacienteAsync(paciente.Id);
                        Console.WriteLine("\n✅ Paciente eliminado exitosamente.");
                    }
                    else
                    {
                        Console.WriteLine("\n❌ Operación cancelada.");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n❌ Error: {ex.Message}");
            }

            Console.WriteLine("\nPresione Enter para continuar...");
            Console.ReadLine();
        }

        static async Task MostrarMenuFichas()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   FICHAS MÉDICAS");
            Console.WriteLine("═══════════════════════════════════════\n");
            Console.WriteLine("Funcionalidad en desarrollo...");
            Console.WriteLine("\nPresione Enter para volver...");
            Console.ReadLine();
        }

        static async Task MostrarReportes()
        {
            Console.Clear();
            Console.WriteLine("═══════════════════════════════════════");
            Console.WriteLine("   REPORTES");
            Console.WriteLine("═══════════════════════════════════════\n");

            try
            {
                var pacientes = await _pacienteService!.ObtenerTodosPacientesAsync();
                var totalPacientes = pacientes?.Count() ?? 0;

                Console.WriteLine($"Total de pacientes: {totalPacientes}");
                Console.WriteLine($"Fecha del reporte: {DateTime.Now:dd/MM/yyyy HH:mm:ss}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }

            Console.WriteLine("\nPresione Enter para volver...");
            Console.ReadLine();
        }
    }
}

