package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.service.PacienteService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PacienteFormController {

    private final PacienteService pacienteService;

    @FXML
    private Text titleText;
    @FXML
    private TextField rutField;
    @FXML
    private Spinner<Integer> edadSpinner;
    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidoField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField ciudadField;
    @FXML
    private TextField direccionField;
    @FXML
    private TextArea notasField;

    private Paciente currentPaciente;
    private boolean saved = false;

    @FXML
    public void initialize() {
        // Configurar spinner de edad: valores de 1 a 120
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 30);
        edadSpinner.setValueFactory(valueFactory);
        edadSpinner.setEditable(true);
    }

    public void setPaciente(Paciente paciente) {
        this.currentPaciente = paciente;
        if (paciente.getId() != null) {
            titleText.setText("Editar Paciente");
            rutField.setText(paciente.getRut());
            
            // Calcular edad desde la fecha de nacimiento
            if (paciente.getFechaNacimiento() != null) {
                int edad = calcularEdad(paciente.getFechaNacimiento());
                edadSpinner.getValueFactory().setValue(edad);
            }
            
            nombreField.setText(paciente.getNombre());
            apellidoField.setText(paciente.getApellido());
            emailField.setText(paciente.getEmail());
            telefonoField.setText(paciente.getTelefono());
            ciudadField.setText(paciente.getCiudad());
            direccionField.setText(paciente.getDireccion());
            notasField.setText(paciente.getNotasClinicas());
        } else {
            titleText.setText("Nuevo Paciente");
        }
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void handleSave() {
        try {
            if (currentPaciente == null)
                currentPaciente = new Paciente();

            currentPaciente.setRut(rutField.getText());
            
            // Calcular fecha de nacimiento desde la edad
            int edad = edadSpinner.getValue();
            LocalDate fechaNacimiento = LocalDate.now().minusYears(edad);
            currentPaciente.setFechaNacimiento(fechaNacimiento);
            
            currentPaciente.setNombre(nombreField.getText());
            currentPaciente.setApellido(apellidoField.getText());
            currentPaciente.setEmail(emailField.getText());
            currentPaciente.setTelefono(telefonoField.getText());
            currentPaciente.setCiudad(ciudadField.getText());
            currentPaciente.setDireccion(direccionField.getText());
            currentPaciente.setNotasClinicas(notasField.getText());

            pacienteService.save(currentPaciente);
            saved = true;
            closeWindow();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar el paciente");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rutField.getScene().getWindow();
        stage.close();
    }

    /**
     * Calcula la edad actual basada en una fecha de nacimiento
     */
    private int calcularEdad(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNacimiento.getYear();
        
        // Ajustar si aún no ha cumplido años este año
        if (hoy.getMonthValue() < fechaNacimiento.getMonthValue() ||
            (hoy.getMonthValue() == fechaNacimiento.getMonthValue() && 
             hoy.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        
        return edad;
    }
}
