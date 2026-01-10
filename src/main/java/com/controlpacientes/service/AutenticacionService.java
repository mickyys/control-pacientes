package com.controlpacientes.service;

import com.controlpacientes.model.Usuario;
import com.controlpacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutenticacionService {
    
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public Optional<Usuario> autenticar(String rut, String clave) {
        Optional<Usuario> usuario = usuarioRepository.findByRut(rut);
        
        if (usuario.isPresent() && passwordEncoder.matches(clave, usuario.get().getClave())) {
            log.info("Usuario autenticado: {}", rut);
            return usuario;
        }
        
        log.warn("Intento de autenticación fallido para RUT: {}", rut);
        return Optional.empty();
    }
}
