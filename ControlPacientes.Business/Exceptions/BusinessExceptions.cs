using System;

namespace ControlPacientes.Business.Exceptions;

/// <summary>
/// Excepción personalizada para errores de validación
/// </summary>
public class ValidationException : Exception
{
    public ValidationException(string message) : base(message)
    {
    }
}

/// <summary>
/// Excepción personalizada para recursos no encontrados
/// </summary>
public class EntityNotFoundException : Exception
{
    public EntityNotFoundException(string message) : base(message)
    {
    }
}

/// <summary>
/// Excepción personalizada para operaciones duplicadas
/// </summary>
public class DuplicateEntityException : Exception
{
    public DuplicateEntityException(string message) : base(message)
    {
    }
}
