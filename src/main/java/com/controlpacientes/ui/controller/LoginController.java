package com.controlpacientes.ui.controller;

import com.controlpacientes.service.AutenticacionService;
import com.controlpacientes.service.UsuarioActualService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    
    private final AutenticacionService autenticacionService;
    private final UsuarioActualService usuarioActualService;
    private final ApplicationContext applicationContext;
    
    @FXML private TextField tfRut;
    @FXML private PasswordField pfClave;
    @FXML private Label lblError;
    
    @FXML
    public void initialize() {
        lblError.setText("");
        setupRutFormatter(tfRut);
    }
    
    private void setupRutFormatter(TextField rutField) {
        // Formatear cuando pierde el foco
        rutField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !rutField.getText().isEmpty()) {
                String rutLimpio = rutField.getText().replaceAll("[^0-9Kk]", "");
                if (!rutLimpio.isEmpty()) {
                    String rutFormateado = formatearRut(rutLimpio);
                    rutField.setText(rutFormateado);
                }
            }
        });
    }
    
    private String formatearRut(String rut) {
        if (rut == null || rut.isEmpty()) {
            return "";
        }
        
        // Eliminar puntos y guiones para procesar
        String rutLimpio = rut.replaceAll("[^0-9Kk]", "");
        
        if (rutLimpio.length() < 2) {
            return rutLimpio;
        }
        
        // Formato: XX.XXX.XXX-X (ej: 12.345.678-9)
        String digitos = rutLimpio.substring(0, rutLimpio.length() - 1);
        String verificador = rutLimpio.substring(rutLimpio.length() - 1);
        
        // Agregar puntos cada 3 dígitos de derecha a izquierda
        StringBuilder rutFormateado = new StringBuilder();
        int len = digitos.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) {
                rutFormateado.append(".");
            }
            rutFormateado.append(digitos.charAt(i));
        }
        
        return rutFormateado.toString() + "-" + verificador;
    }
    
    @FXML
    public void handleLogin() {
        String rut = tfRut.getText().trim().replaceAll("[^0-9Kk]", "");
        String clave = pfClave.getText();
        
        if (rut.isEmpty() || clave.isEmpty()) {
            lblError.setText("Por favor ingrese RUT y clave");
            return;
        }
        
        var usuarioOpt = autenticacionService.autenticar(rut, clave);
        if (usuarioOpt.isPresent()) {
            // Autenticación exitosa - guardar usuario actual
            usuarioActualService.setUsuarioAutenticado(usuarioOpt.get());
            lblError.setText("");
            abrirPantallaPrincipal();
        } else {
            // Autenticación fallida
            lblError.setText("RUT o clave incorrectos: " + rut + " " + clave);
            pfClave.clear();
        }
    }
    
    private void abrirPantallaPrincipal() {
        try {
            Stage stage = (Stage) tfRut.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            stage.setTitle("Control de Pacientes");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (Exception e) {
            log.error("Error al cargar pantalla principal", e);
            lblError.setText("Error al cargar la aplicación");
        }
    }
}
