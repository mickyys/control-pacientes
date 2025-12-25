package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.service.PacienteService;
import com.controlpacientes.ui.UINavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MainController {

    private final PacienteService pacienteService;
    private final UINavigator uiNavigator;

    @FXML
    private TextField searchField;
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
    private TableColumn<Paciente, String> colEmail;
    @FXML
    private TableColumn<Paciente, String> colCiudad;
    @FXML
    private TableColumn<Paciente, Void> colAcciones;

    private ObservableList<Paciente> observablePacientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        pacientesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colRut.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRut()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colCiudad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCiudad()));

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

    @FXML
    private void handleShowPacientes() {
        loadPacientes();
    }

    @FXML
    private void handleShowFichas() {
        uiNavigator.openModal("/fxml/fichas_list.fxml", "Todas las Fichas Médicas",
                (FichaMedicaListController controller) -> {
                    controller.loadAllFichas();
                });
    }
}
