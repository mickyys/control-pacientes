using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace ControlPacientes.Data.Repositories;

/// <summary>
/// Interfaz para operaciones CRUD genéricas
/// </summary>
public interface IGenericRepository<T> where T : class
{
    Task<T?> GetByIdAsync(int id);
    
    Task<IEnumerable<T>> GetAllAsync();
    
    Task<T> AddAsync(T entity);
    
    Task<T> UpdateAsync(T entity);
    
    Task DeleteAsync(T entity);
    
    Task<int> CountAsync();
}
