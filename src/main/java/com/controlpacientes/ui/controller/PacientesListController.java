package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.service.PacienteService;
import com.controlpacientes.ui.UINavigator;
import com.controlpacientes.util.RutUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PacientesListController {

    private final PacienteService pacienteService;
    private final UINavigator uiNavigator;

    @FXML
    private TextField searchNombre;
    @FXML
    private TextField searchRut;
    @FXML
    private TextField searchEmail;
    @FXML
    private TextField searchCiudad;
    @FXML
    private TableView<Paciente> pacientesTable;
    @FXML
    private TableColumn<Paciente, String> colRut;
    @FXML
    private TableColumn<Paciente, String> colNombre;
    @FXML
    private TableColumn<Paciente, String> colEdad;
    @FXML
    private TableColumn<Paciente, String> colTelefono;
    @FXML
    private TableColumn<Paciente, String> colCiudad;
    @FXML
    private TableColumn<Paciente, String> colUltimaVisita;
    @FXML
    private TableColumn<Paciente, Void> colAcciones;

    private ObservableList<Paciente> observablePacientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        pacientesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colRut.setCellValueFactory(cellData -> new SimpleStringProperty(RutUtils.formatRut(cellData.getValue().getRut())));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colEdad.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(calcularEdad(cellData.getValue().getFechaNacimiento()))));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        colCiudad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCiudad()));
        colUltimaVisita.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerFechaUltimaVisita(cellData.getValue())));

        addButtonToTable();
        loadPacientes();
    }

    private void addButtonToTable() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button fichasBtn = new Button("Fichas");
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");
            private final HBox container = new HBox(10, fichasBtn, editBtn, deleteBtn);

            {
                fichasBtn.getStyleClass().add("btn-sm-primary");
                editBtn.getStyleClass().add("btn-sm-outline");
                deleteBtn.getStyleClass().add("btn-sm-danger");

                fichasBtn.setOnAction(event -> {
                    Paciente p = getTableView().getItems().get(getIndex());
                    handleShowFichasPaciente(p);
                });
                editBtn.setOnAction(event -> {
                    Paciente p = getTableView().getItems().get(getIndex());
                    handleEditPaciente(p);
                });
                deleteBtn.setOnAction(event -> {
                    Paciente p = getTableView().getItems().get(getIndex());
                    handleDeletePaciente(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadPacientes() {
        List<Paciente> list = pacienteService.findAll();
        list.sort((p1, p2) -> p1.getNombreCompleto().compareToIgnoreCase(p2.getNombreCompleto()));
        observablePacientes.setAll(list);
        pacientesTable.setItems(observablePacientes);
    }

    @FXML
    private void handleSearch() {
        String nombre = searchNombre.getText();
        String rut = searchRut.getText();
        String email = searchEmail.getText();
        String ciudad = searchCiudad.getText();
        
        List<Paciente> list = pacienteService.searchAdvanced(nombre, rut, email, ciudad);
        list.sort((p1, p2) -> p1.getNombreCompleto().compareToIgnoreCase(p2.getNombreCompleto()));
        observablePacientes.setAll(list);
    }

    @FXML
    private void handleClearSearch() {
        searchNombre.clear();
        searchRut.clear();
        searchEmail.clear();
        searchCiudad.clear();
        loadPacientes();
    }

    @FXML
    private void handleNewPaciente() {
        uiNavigator.openModal("/fxml/paciente_form.fxml", "Nuevo Paciente", (PacienteFormController controller) -> {
            controller.setPaciente(new Paciente());
        });
        loadPacientes();
    }

    private void handleEditPaciente(Paciente paciente) {
        uiNavigator.openModal("/fxml/paciente_form.fxml", "Editar Paciente", (PacienteFormController controller) -> {
            controller.setPaciente(paciente);
        });
        loadPacientes();
    }

    private void handleDeletePaciente(Paciente paciente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar a " + paciente.getNombreCompleto() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                pacienteService.delete(paciente.getId());
                loadPacientes();
            }
        });
    }

    private void handleShowFichasPaciente(Paciente paciente) {
        uiNavigator.openModal("/fxml/fichas_list.fxml", "Fichas Médicas - " + paciente.getNombreCompleto(),
                (FichaMedicaListController controller) -> {
                    controller.setPaciente(paciente);
                });
    }

    /**
     * Calcula la edad actual basada en una fecha de nacimiento
     */
    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
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

    /**
     * Obtiene la fecha de la última visita del paciente
     */
    private String obtenerFechaUltimaVisita(Paciente paciente) {
        if (paciente.getFichasMedicas() == null || paciente.getFichasMedicas().isEmpty()) {
            return "-";
        }
        
        return paciente.getFichasMedicas().stream()
                .map(ficha -> ficha.getFechaAtencion())
                .filter(fecha -> fecha != null)
                .max(LocalDateTime::compareTo)
                .map(fecha -> fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .orElse("-");
    }
}
