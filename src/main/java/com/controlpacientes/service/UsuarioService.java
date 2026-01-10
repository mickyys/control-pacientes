package com.controlpacientes.service;

import com.controlpacientes.model.Usuario;
import com.controlpacientes.repository.UsuarioRepository;
import com.controlpacientes.util.RutUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Obtiene los usuarios activos
     */
    public List<Usuario> obtenerActivos() {
        return usuarioRepository.findByActivo(true);
    }
    
    /**
     * Obtiene un usuario por ID
     */
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * Obtiene un usuario por RUT
     */
    public Optional<Usuario> obtenerPorRut(String rut) {
        return usuarioRepository.findByRut(rut);
    }
    
    /**
     * Crea un nuevo usuario con contraseña encriptada
     */
    @Transactional
    public Usuario crear(Usuario usuario) {
        // Normalizar RUT (convertir a mayúscula, eliminar separadores)
        usuario.setRut(RutUtils.normalizeRut(usuario.getRut()));
        
        // Capitalizar nombre y apellido
        usuario.setNombre(RutUtils.capitalize(usuario.getNombre()));
        usuario.setApellido(RutUtils.capitalize(usuario.getApellido()));
        
        // Validar que no exista otro usuario con el mismo RUT
        if (usuarioRepository.findByRut(usuario.getRut()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el RUT: " + usuario.getRut());
        }
        
        // Encriptar la contraseña
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        usuario.setActivo(true);
        
        Usuario saved = usuarioRepository.save(usuario);
        log.info("USUARIO_CREADO - RUT: {}, Nombre: {} {}, Rol: {}", 
            usuario.getRut(), 
            usuario.getNombre(), 
            usuario.getApellido(),
            usuario.getRol());
        return saved;
    }
    
    /**
     * Actualiza un usuario existente (sin cambiar contraseña)
     */
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        // Normalizar RUT (convertir a mayúscula, eliminar separadores)
        usuario.setRut(RutUtils.normalizeRut(usuario.getRut()));
        
        // Capitalizar nombre y apellido
        usuario.setNombre(RutUtils.capitalize(usuario.getNombre()));
        usuario.setApellido(RutUtils.capitalize(usuario.getApellido()));
        
        Optional<Usuario> existente = usuarioRepository.findById(usuario.getId());
        
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getId());
        }
        
        Usuario usuarioExistente = existente.get();
        
        // Validar que el nuevo RUT no esté en uso por otro usuario
        if (!usuarioExistente.getRut().equals(usuario.getRut()) &&
            usuarioRepository.findByRut(usuario.getRut()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro usuario con el RUT: " + usuario.getRut());
        }
        
        usuarioExistente.setRut(usuario.getRut());
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setApellido(usuario.getApellido());
        usuarioExistente.setActivo(usuario.isActivo());
        
        Usuario updated = usuarioRepository.save(usuarioExistente);
        log.info("USUARIO_ACTUALIZADO - RUT: {}, Nombre: {} {}", 
            usuario.getRut(), 
            usuario.getNombre(), 
            usuario.getApellido());
        return updated;
    }
    
    /**
     * Cambia la contraseña de un usuario
     */
    @Transactional
    public void cambiarContrasena(Long usuarioId, String claveActual, String claveNueva) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario u = usuario.get();
        
        // Validar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(claveActual, u.getClave())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        // Validar que la nueva contraseña no esté vacía
        if (claveNueva == null || claveNueva.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        
        // Encriptar y guardar la nueva contraseña
        u.setClave(passwordEncoder.encode(claveNueva));
        usuarioRepository.save(u);
        log.info("Contraseña cambiada para usuario: {}", u.getRut());
    }
    
    /**
     * Reinicia la contraseña de un usuario (solo para administradores)
     */
    @Transactional
    public void reiniciarContrasena(Long usuarioId, String claveNueva) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        if (claveNueva == null || claveNueva.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        
        Usuario u = usuario.get();
        u.setClave(passwordEncoder.encode(claveNueva));
        usuarioRepository.save(u);
        log.info("Contraseña reiniciada para usuario: {}", u.getRut());
    }
    
    /**
     * Desactiva un usuario
     */
    @Transactional
    public void desactivar(Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario u = usuario.get();
        u.setActivo(false);
        usuarioRepository.save(u);
        log.info("Usuario desactivado: {}", u.getRut());
    }
    
    /**
     * Activa un usuario
     */
    @Transactional
    public void activar(Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario u = usuario.get();
        u.setActivo(true);
        usuarioRepository.save(u);
        log.info("Usuario activado: {}", u.getRut());
    }
    
    /**
     * Elimina un usuario
     */
    @Transactional
    public void eliminar(Long usuarioId) {
        usuarioRepository.deleteById(usuarioId);
        log.info("Usuario eliminado con ID: {}", usuarioId);
    }
}