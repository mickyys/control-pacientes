package com.controlpacientes.ui.controller;

import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.model.MedicamentoAtencion;
import com.controlpacientes.model.Paciente;
import com.controlpacientes.service.FichaMedicaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class FichaMedicaFormController {

    private final FichaMedicaService fichaMedicaService;

    @FXML
    private Text titleText;
    @FXML
    private DatePicker fechaAtencionPicker;
    @FXML
    private TextField profesionalField;
    @FXML
    private TextArea motivoField;
    @FXML
    private TextArea diagnosticoField;

    @FXML
    private TableView<MedicamentoAtencion> medicamentosTable;
    @FXML
    private TableColumn<MedicamentoAtencion, String> colMedNombre;
    @FXML
    private TableColumn<MedicamentoAtencion, String> colMedDosis;
    @FXML
    private TableColumn<MedicamentoAtencion, String> colMedFrecuencia;
    @FXML
    private TableColumn<MedicamentoAtencion, String> colMedDuracion;
    @FXML
    private TableColumn<MedicamentoAtencion, String> colMedIndicaciones;
    @FXML
    private TableColumn<MedicamentoAtencion, Void> colMedAcciones;
    @FXML
    private Button btnGuardar;

    private Paciente currentPaciente;
    private FichaMedica currentFicha;
    private ObservableList<MedicamentoAtencion> observableMedicamentos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        medicamentosTable.setEditable(true);
        medicamentosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colMedNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreMedicamento()));
        colMedNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedNombre.setOnEditCommit(e -> e.getRowValue().setNombreMedicamento(e.getNewValue()));

        colMedDosis.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDosis()));
        colMedDosis.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedDosis.setOnEditCommit(e -> e.getRowValue().setDosis(e.getNewValue()));

        colMedFrecuencia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFrecuencia()));
        colMedFrecuencia.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedFrecuencia.setOnEditCommit(e -> e.getRowValue().setFrecuencia(e.getNewValue()));

        colMedDuracion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDuracion()));
        colMedDuracion.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedDuracion.setOnEditCommit(e -> e.getRowValue().setDuracion(e.getNewValue()));

        colMedIndicaciones.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndicaciones()));
        colMedIndicaciones.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedIndicaciones.setOnEditCommit(e -> e.getRowValue().setIndicaciones(e.getNewValue()));

        colMedAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("X");
            {
                deleteBtn.getStyleClass().add("btn-sm-danger");
                deleteBtn.setOnAction(event -> {
                    MedicamentoAtencion m = getTableView().getItems().get(getIndex());
                    observableMedicamentos.remove(m);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        medicamentosTable.setItems(observableMedicamentos);
    }

    public void setPaciente(Paciente paciente) {
        this.currentPaciente = paciente;
    }

    public void setFicha(FichaMedica ficha) {
        this.currentFicha = ficha;
        if (ficha != null && ficha.getId() != null) {
            titleText.setText("Editar Ficha Médica");
            fechaAtencionPicker.setValue(ficha.getFechaAtencion().toLocalDate());
            profesionalField.setText(ficha.getProfesionalNombre());
            motivoField.setText(ficha.getMotivoConsulta());
            diagnosticoField.setText(ficha.getDiagnostico());
            observableMedicamentos.setAll(ficha.getMedicamentos());
        } else {
            titleText.setText("Nueva Ficha Médica");
            fechaAtencionPicker.setValue(java.time.LocalDate.now());
            profesionalField.setText("Emilio Alcaino");
        }
    }

    @FXML
    private void handleAddMedicamento() {
        MedicamentoAtencion nuevo = new MedicamentoAtencion();
        nuevo.setNombreMedicamento("Nuevo Medicamento");
        observableMedicamentos.add(nuevo);
    }

    @FXML
    private void handleSave() {
        try {
            if (currentFicha == null)
                currentFicha = new FichaMedica();

            currentFicha.setPaciente(currentPaciente);
            // Si es una nueva ficha, usar la hora actual. Si es edición, mantener la fecha original con hora actual
            if (currentFicha.getId() == null) {
                currentFicha.setFechaAtencion(java.time.LocalDateTime.now());
            } else {
                currentFicha.setFechaAtencion(fechaAtencionPicker.getValue().atTime(java.time.LocalTime.now()));
            }
            currentFicha.setProfesionalNombre(profesionalField.getText());
            currentFicha.setMotivoConsulta(motivoField.getText());
            currentFicha.setDiagnostico(diagnosticoField.getText());

            currentFicha.setMedicamentos(new ArrayList<>(observableMedicamentos));

            fichaMedicaService.save(currentFicha);
            closeWindow();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar la ficha");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) btnGuardar.getScene().getWindow()).close();
    }
}
