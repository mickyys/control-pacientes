package com.controlpacientes.service;

import com.controlpacientes.model.Usuario;
import com.controlpacientes.model.RolUsuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class UsuarioActualService {
    
    private Usuario usuarioActual;
    
    public void setUsuarioAutenticado(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    public Usuario getUsuarioAutenticado() {
        return usuarioActual;
    }
    
    public boolean isAdministrador() {
        return usuarioActual != null && usuarioActual.getRol() == RolUsuario.ADMINISTRADOR;
    }
    
    public boolean isDoctor() {
        return usuarioActual != null && usuarioActual.getRol() == RolUsuario.DOCTOR;
    }
    
    public void logout() {
        usuarioActual = null;
    }
}
