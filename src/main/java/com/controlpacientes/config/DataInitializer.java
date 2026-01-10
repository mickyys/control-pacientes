package com.controlpacientes.config;

import com.controlpacientes.model.Usuario;
import com.controlpacientes.model.RolUsuario;
import com.controlpacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initializeData(UsuarioRepository usuarioRepository) {
        return args -> {
            // Limpiar RUTs con guión en los usuarios existentes
            var usuarios = usuarioRepository.findAll();
            boolean actualizado = false;
            for (Usuario usuario : usuarios) {
                if (usuario.getRut().contains("-")) {
                    usuario.setRut(usuario.getRut().replaceAll("-", ""));
                    usuarioRepository.save(usuario);
                    actualizado = true;
                }
            }
            if (actualizado) {
                log.info("RUTs actualizados (removidos guiones)");
            }
            
            // Verificar si ya existen usuarios
            if (usuarioRepository.count() == 0) {
                log.info("Inicializando usuarios de demostración...");
                
                // Crear usuario administrador
                Usuario admin = new Usuario();
                admin.setRut("123456789");
                admin.setNombre("Administrador");
                admin.setApellido("Sistema");
                admin.setClave(passwordEncoder.encode("admin"));
                admin.setActivo(true);
                admin.setRol(RolUsuario.ADMINISTRADOR);
                
                // Crear usuario doctor
                Usuario doctor = new Usuario();
                doctor.setRut("876543210");
                doctor.setNombre("Doctor");
                doctor.setApellido("Sistema");
                doctor.setClave(passwordEncoder.encode("doctor"));
                doctor.setActivo(true);
                doctor.setRol(RolUsuario.DOCTOR);
                
                usuarioRepository.save(admin);
                usuarioRepository.save(doctor);
                
                log.info("Usuarios de demostración creados");
                log.info("- Administrador (RUT: 123456789, Clave: admin)");
                log.info("- Doctor (RUT: 876543210, Clave: doctor)");
            } else {
                log.info("Usuarios ya existen en la base de datos");
            }
        };
    }
}
