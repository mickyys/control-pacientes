package com.controlpacientes.model;

public enum RolUsuario {
    ADMINISTRADOR("Administrador", "Puede gestionar usuarios y todas las funcionalidades"),
    DOCTOR("Doctor", "Puede gestionar pacientes y fichas médicas");
    
    private final String displayName;
    private final String descripcion;
    
    RolUsuario(String displayName, String descripcion) {
        this.displayName = displayName;
        this.descripcion = descripcion;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
