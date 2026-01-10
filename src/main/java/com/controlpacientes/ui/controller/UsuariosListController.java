package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Usuario;
import com.controlpacientes.model.RolUsuario;
import com.controlpacientes.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuariosListController {
    
    private final UsuarioService usuarioService;
    private final ApplicationContext applicationContext;
    
    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, Long> tcId;
    @FXML private TableColumn<Usuario, String> tcRut;
    @FXML private TableColumn<Usuario, String> tcNombre;
    @FXML private TableColumn<Usuario, RolUsuario> tcRol;
    @FXML private TableColumn<Usuario, Boolean> tcActivo;
    @FXML private TableColumn<Usuario, LocalDateTime> tcFechaCreacion;
    @FXML private TableColumn<Usuario, Void> tcAcciones;
    
    @FXML private Button btnNuevoUsuario;
    @FXML private Button btnActualizar;
    @FXML private TextField tfBuscar;
    @FXML private Label lblTotalUsuarios;
    
    private ObservableList<Usuario> usuariosData;
    private List<Usuario> usuariosFull;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableActions();
        cargarUsuarios();
        setupSearchListener();
    }
    
    private void setupTableColumns() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcRut.setCellValueFactory(new PropertyValueFactory<>("rut"));
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        tcActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        // Columna de rol con nombre visible
        tcRol.setCellFactory(column -> new TableCell<Usuario, RolUsuario>() {
            @Override
            protected void updateItem(RolUsuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        
        // Columna de fecha de creación con formato
        tcFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        tcFechaCreacion.setCellFactory(column -> new TableCell<Usuario, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });
        
        // Columna de estado (activo/inactivo)
        tcActivo.setCellFactory(column -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Activo" : "Inactivo");
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });
        
        // Columna de acciones
        setupTableActions();
    }
    
    private void setupTableActions() {
        tcAcciones.setCellFactory(param -> new TableCell<Usuario, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnCambiarClave = new Button("Cambiar Clave");
            private final Button btnDesactivar = new Button("Desactivar");
            
            {
                btnEditar.setStyle("-fx-padding: 5px 10px; -fx-font-size: 12px;");
                btnCambiarClave.setStyle("-fx-padding: 5px 10px; -fx-font-size: 12px;");
                btnDesactivar.setStyle("-fx-padding: 5px 10px; -fx-font-size: 12px;");
                
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableRow().getItem();
                    if (usuario != null) {
                        abrirFormularioEdicion(usuario);
                    }
                });
                
                btnCambiarClave.setOnAction(event -> {
                    Usuario usuario = getTableRow().getItem();
                    if (usuario != null) {
                        abrirDialogoCambiarClave(usuario);
                    }
                });
                
                btnDesactivar.setOnAction(event -> {
                    Usuario usuario = getTableRow().getItem();
                    if (usuario != null) {
                        toggleActivoUsuario(usuario);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableRow().getItem();
                    btnDesactivar.setText(usuario.isActivo() ? "Desactivar" : "Activar");
                    setGraphic(new HBox(5, btnEditar, btnCambiarClave, btnDesactivar));
                }
            }
        });
    }
    
    private void setupSearchListener() {
        tfBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarUsuarios(newValue);
        });
    }
    
    private void filtrarUsuarios(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            usuariosData.setAll(usuariosFull);
        } else {
            String filtro = texto.toLowerCase();
            List<Usuario> filtrados = usuariosFull.stream()
                .filter(u -> u.getRut().toLowerCase().contains(filtro) ||
                           u.getNombre().toLowerCase().contains(filtro))
                .toList();
            usuariosData.setAll(filtrados);
        }
    }
    
    @FXML
    private void cargarUsuarios() {
        try {
            usuariosFull = usuarioService.obtenerTodos();
            usuariosData = FXCollections.observableArrayList(usuariosFull);
            tvUsuarios.setItems(usuariosData);
            actualizarLabelTotal();
        } catch (Exception e) {
            log.error("Error al cargar usuarios", e);
            mostrarError("Error", "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }
    
    private void actualizarLabelTotal() {
        lblTotalUsuarios.setText("Total: " + usuariosData.size() + " usuarios");
    }
    
    @FXML
    private void handleNuevoUsuario() {
        abrirFormularioCreacion();
    }
    
    @FXML
    private void handleActualizar() {
        cargarUsuarios();
    }
    
    private void abrirFormularioCreacion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usuario-form.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            UsuarioFormController controller = loader.getController();
            controller.setModoCreacion(true);
            controller.setOnGuardar(usuario -> {
                cargarUsuarios();
            });
            
            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            log.error("Error al abrir formulario de creación", e);
            mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
    
    private void abrirFormularioEdicion(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usuario-form.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            UsuarioFormController controller = loader.getController();
            controller.setModoCreacion(false);
            controller.setUsuario(usuario);
            controller.setOnGuardar(usuarioActualizado -> {
                cargarUsuarios();
            });
            
            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            log.error("Error al abrir formulario de edición", e);
            mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
    
    private void abrirDialogoCambiarClave(Usuario usuario) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Cambiar contraseña para: " + usuario.getNombre());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        PasswordField pfClaveActual = new PasswordField();
        pfClaveActual.setPromptText("Contraseña actual");
        
        PasswordField pfClaveNueva = new PasswordField();
        pfClaveNueva.setPromptText("Nueva contraseña");
        
        PasswordField pfConfirmarClave = new PasswordField();
        pfConfirmarClave.setPromptText("Confirmar nueva contraseña");
        
        content.getChildren().addAll(
            new Label("Contraseña actual:"),
            pfClaveActual,
            new Label("Nueva contraseña:"),
            pfClaveNueva,
            new Label("Confirmar contraseña:"),
            pfConfirmarClave
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String claveActual = pfClaveActual.getText();
            String claveNueva = pfClaveNueva.getText();
            String confirmarClave = pfConfirmarClave.getText();
            
            if (claveActual.isEmpty() || claveNueva.isEmpty()) {
                mostrarError("Error", "Los campos no pueden estar vacíos");
                return;
            }
            
            if (!claveNueva.equals(confirmarClave)) {
                mostrarError("Error", "Las contraseñas no coinciden");
                return;
            }
            
            try {
                usuarioService.cambiarContrasena(usuario.getId(), claveActual, claveNueva);
                mostrarInfo("Éxito", "Contraseña cambiada exitosamente");
            } catch (IllegalArgumentException e) {
                mostrarError("Error", e.getMessage());
            } catch (Exception e) {
                log.error("Error al cambiar contraseña", e);
                mostrarError("Error", "Error al cambiar la contraseña: " + e.getMessage());
            }
        }
    }
    
    private void toggleActivoUsuario(Usuario usuario) {
        String mensaje = usuario.isActivo() ? 
            "¿Desactivar usuario " + usuario.getNombre() + "?" :
            "¿Activar usuario " + usuario.getNombre() + "?";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText(mensaje);
        alert.setContentText("Esta acción se puede revertir posteriormente");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (usuario.isActivo()) {
                    usuarioService.desactivar(usuario.getId());
                } else {
                    usuarioService.activar(usuario.getId());
                }
                cargarUsuarios();
                mostrarInfo("Éxito", "Usuario actualizado correctamente");
            } catch (Exception e) {
                log.error("Error al actualizar usuario", e);
                mostrarError("Error", "Error al actualizar el usuario: " + e.getMessage());
            }
        }
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