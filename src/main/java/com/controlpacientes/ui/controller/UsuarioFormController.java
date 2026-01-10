package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Usuario;
import com.controlpacientes.model.RolUsuario;
import com.controlpacientes.service.UsuarioService;
import com.controlpacientes.util.RutUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioFormController {
    
    private final UsuarioService usuarioService;
    
    @FXML private TextField tfRut;
    @FXML private TextField tfNombre;
    @FXML private PasswordField pfClave;
    @FXML private PasswordField pfConfirmarClave;
    @FXML private CheckBox cbActivo;
    @FXML private ComboBox<RolUsuario> cbRol;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Label lblClaveLabel;
    @FXML private VBox hboxClave;
    
    private Usuario usuarioActual;
    private boolean modoCreacion = true;
    private Consumer<Usuario> onGuardar;
    
    @FXML
    public void initialize() {
        setupRutFormatter();
        setupRolComboBox();
        btnGuardar.setOnAction(e -> handleGuardar());
        btnCancelar.setOnAction(e -> handleCancelar());
    }
    
    private void setupRolComboBox() {
        cbRol.getItems().addAll(RolUsuario.values());
        cbRol.setConverter(new javafx.util.StringConverter<RolUsuario>() {
            @Override
            public String toString(RolUsuario rol) {
                return rol != null ? rol.getDisplayName() : "";
            }
            
            @Override
            public RolUsuario fromString(String string) {
                return RolUsuario.valueOf(string);
            }
        });
        cbRol.getSelectionModel().selectFirst();
    }
    
    public void setModoCreacion(boolean modo) {
        this.modoCreacion = modo;
        
        if (modo) {
            lblClaveLabel.setText("Contraseña *");
            hboxClave.setVisible(true);
            hboxClave.setManaged(true);
        } else {
            lblClaveLabel.setText("Nueva Contraseña (dejar vacío para no cambiar)");
            hboxClave.setVisible(true);
            hboxClave.setManaged(true);
        }
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        this.modoCreacion = false;
        
        tfRut.setText(usuario.getRut());
        tfNombre.setText(usuario.getNombre());
        cbActivo.setSelected(usuario.isActivo());
        cbRol.getSelectionModel().select(usuario.getRol());
        
        // En modo edición, los campos de contraseña son opcionales
        tfRut.setDisable(false);
        pfClave.setPromptText("Dejar vacío para mantener la contraseña actual");
        pfConfirmarClave.setPromptText("Dejar vacío para mantener la contraseña actual");
    }
    
    public void setOnGuardar(Consumer<Usuario> callback) {
        this.onGuardar = callback;
    }
    
    private void setupRutFormatter() {
        tfRut.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !tfRut.getText().isEmpty()) {
                String rutLimpio = tfRut.getText().replaceAll("[^0-9Kk]", "");
                if (!rutLimpio.isEmpty()) {
                    String rutFormateado = RutUtils.formatRut(rutLimpio);
                    tfRut.setText(rutFormateado);
                }
            }
        });
    }
    
    @FXML
    private void handleGuardar() {
        // Validar campos requeridos
        if (!validarFormulario()) {
            return;
        }
        
        try {
            if (modoCreacion) {
                crearUsuario();
            } else {
                actualizarUsuario();
            }
            
            if (onGuardar != null) {
                onGuardar.accept(usuarioActual);
            }
            
            cerrarVentana();
        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        } catch (Exception e) {
            log.error("Error al guardar usuario", e);
            mostrarError("Error", "Error al guardar el usuario: " + e.getMessage());
        }
    }
    
    private void crearUsuario() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRut(limpiarRut(tfRut.getText().trim()));
        nuevoUsuario.setNombre(tfNombre.getText().trim());
        nuevoUsuario.setClave(pfClave.getText());
        nuevoUsuario.setActivo(cbActivo.isSelected());
        nuevoUsuario.setRol(cbRol.getSelectionModel().getSelectedItem());
        
        usuarioActual = usuarioService.crear(nuevoUsuario);
        mostrarInfo("Éxito", "Usuario creado correctamente");
    }
    
    private void actualizarUsuario() {
        // Actualizar información básica
        usuarioActual.setRut(limpiarRut(tfRut.getText().trim()));
        usuarioActual.setNombre(tfNombre.getText().trim());
        usuarioActual.setActivo(cbActivo.isSelected());
        usuarioActual.setRol(cbRol.getSelectionModel().getSelectedItem());
        
        // Si se proporciona una nueva contraseña, cambiarla
        if (!pfClave.getText().isEmpty()) {
            if (pfClave.getText().equals(pfConfirmarClave.getText())) {
                usuarioService.reiniciarContrasena(usuarioActual.getId(), pfClave.getText());
            } else {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
        }
        
        // Guardar cambios
        usuarioActual = usuarioService.actualizar(usuarioActual);
        mostrarInfo("Éxito", "Usuario actualizado correctamente");
    }
    
    private String limpiarRut(String rut) {
        // Elimina puntos y guión del RUT para guardar en BD
        return rut.replaceAll("[.-]", "");
    }
    
    private boolean validarFormulario() {
        String rut = tfRut.getText().trim();
        String nombre = tfNombre.getText().trim();
        String clave = pfClave.getText();
        String confirmarClave = pfConfirmarClave.getText();
        
        // Validaciones comunes
        if (rut.isEmpty()) {
            mostrarError("Error", "El RUT es requerido");
            return false;
        }
        
        if (nombre.isEmpty()) {
            mostrarError("Error", "El nombre es requerido");
            return false;
        }
        
        // Validaciones específicas por modo
        if (modoCreacion) {
            if (clave.isEmpty()) {
                mostrarError("Error", "La contraseña es requerida");
                return false;
            }
            
            if (!clave.equals(confirmarClave)) {
                mostrarError("Error", "Las contraseñas no coinciden");
                return false;
            }
            
            if (clave.length() < 4) {
                mostrarError("Error", "La contraseña debe tener al menos 4 caracteres");
                return false;
            }
        } else {
            // En modo edición, si hay contraseña nueva, debe coincidir
            if (!clave.isEmpty() && !clave.equals(confirmarClave)) {
                mostrarError("Error", "Las contraseñas no coinciden");
                return false;
            }
            
            if (!clave.isEmpty() && clave.length() < 4) {
                mostrarError("Error", "La contraseña debe tener al menos 4 caracteres");
                return false;
            }
        }
        
        return true;
    }
    
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}