using Microsoft.EntityFrameworkCore;
using ControlPacientes.Core.Models;

namespace ControlPacientes.Data.Context;

/// <summary>
/// Contexto de Entity Framework Core para acceso a datos
/// </summary>
public class ApplicationDbContext : DbContext
{
    public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options)
    {
    }

    public DbSet<Paciente> Pacientes { get; set; }
    
    public DbSet<FichaMedica> FichasMedicas { get; set; }
    
    public DbSet<MedicamentoAtencion> MedicamentosAtencion { get; set; }

    public DbSet<Ciudad> Ciudades { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configuración de Paciente
        modelBuilder.Entity<Paciente>(entity =>
        {
            entity.HasKey(e => e.Id);
            
            entity.Property(e => e.Rut)
                .IsRequired()
                .HasMaxLength(12);
            
            entity.Property(e => e.Nombre)
                .IsRequired()
                .HasMaxLength(100);
            
            entity.Property(e => e.Apellido)
                .IsRequired()
                .HasMaxLength(100);
            
            entity.Property(e => e.Email)
                .HasMaxLength(100);
            
            entity.Property(e => e.Telefono)
                .HasMaxLength(20);
            
            entity.Property(e => e.Ciudad)
                .HasMaxLength(100);
            
            entity.Property(e => e.Direccion)
                .HasMaxLength(200);
            
            entity.Property(e => e.NotasClinicas)
                .HasColumnType("TEXT");
            
            // Índices para búsquedas rápidas
            entity.HasIndex(e => e.Rut).IsUnique();
            entity.HasIndex(e => e.Nombre);
            entity.HasIndex(e => e.Ciudad);
            
            // Relación con fichas médicas
            entity.HasMany(e => e.FichasMedicas)
                .WithOne(f => f.Paciente)
                .HasForeignKey(f => f.PacienteId)
                .OnDelete(DeleteBehavior.Cascade);
        });

        // Configuración de FichaMedica
        modelBuilder.Entity<FichaMedica>(entity =>
        {
            entity.HasKey(e => e.Id);
            
            entity.Property(e => e.ProfesionalNombre)
                .IsRequired()
                .HasMaxLength(100);
            
            entity.Property(e => e.MotivoConsulta)
                .HasMaxLength(200);
            
            entity.Property(e => e.Diagnostico)
                .HasColumnType("TEXT");
            
            entity.Property(e => e.Tratamiento)
                .HasColumnType("TEXT");
            
            entity.Property(e => e.Notas)
                .HasColumnType("TEXT");
            
            // Índices
            entity.HasIndex(e => e.PacienteId);
            entity.HasIndex(e => e.FechaAtencion);
            
            // Relación con medicamentos
            entity.HasMany(e => e.Medicamentos)
                .WithOne(m => m.FichaMedica)
                .HasForeignKey(m => m.FichaMedicaId)
                .OnDelete(DeleteBehavior.Cascade);
        });

        // Configuración de MedicamentoAtencion
        modelBuilder.Entity<MedicamentoAtencion>(entity =>
        {
            entity.HasKey(e => e.Id);
            
            entity.Property(e => e.NombreMedicamento)
                .IsRequired()
                .HasMaxLength(150);
            
            entity.Property(e => e.Dosis)
                .HasMaxLength(100);
            
            entity.Property(e => e.Frecuencia)
                .HasMaxLength(100);
            
            entity.Property(e => e.Duracion)
                .HasMaxLength(100);
            
            entity.Property(e => e.Indicaciones)
                .HasColumnType("TEXT");
            
            // Índice
            entity.HasIndex(e => e.FichaMedicaId);
        });

        // Configuración de Ciudad
        modelBuilder.Entity<Ciudad>(entity =>
        {
            entity.HasKey(e => e.Id);
            
            entity.Property(e => e.Nombre)
                .IsRequired()
                .HasMaxLength(100);
            
            entity.HasIndex(e => e.Nombre).IsUnique();
        });
    }
}
