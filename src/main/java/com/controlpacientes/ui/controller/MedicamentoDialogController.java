package com.controlpacientes.ui.controller;

import com.controlpacientes.model.MedicamentoAtencion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoDialogController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField dosisField;
    @FXML
    private TextField frecuenciaField;
    @FXML
    private TextField duracionField;
    @FXML
    private TextField cantidadField;
    @FXML
    private Button btnAgregar;

    private MedicamentoAtencion medicamento;

    public MedicamentoAtencion getMedicamento() {
        return medicamento;
    }

    @FXML
    private void handleAgregar() {
        if (!validarCampos()) {
            return;
        }

        medicamento = MedicamentoAtencion.builder()
                .nombreMedicamento(nombreField.getText().trim())
                .dosis(dosisField.getText().trim())
                .frecuencia(frecuenciaField.getText().trim())
                .duracion(duracionField.getText().trim())
                .cantidadRecetar(parseCantidad())
                .build();

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        medicamento = null;
        closeWindow();
    }

    private boolean validarCampos() {
        if (nombreField.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese el nombre del medicamento");
            return false;
        }
        if (dosisField.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese la dosis");
            return false;
        }
        if (frecuenciaField.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese la frecuencia");
            return false;
        }
        if (duracionField.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese la duración");
            return false;
        }
        return true;
    }

    private Integer parseCantidad() {
        try {
            String cantidad = cantidadField.getText().trim();
            return cantidad.isEmpty() ? 1 : Integer.parseInt(cantidad);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Campos Incompletos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void closeWindow() {
        ((Stage) btnAgregar.getScene().getWindow()).close();
    }
}
