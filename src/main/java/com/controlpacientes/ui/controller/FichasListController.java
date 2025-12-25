package com.controlpacientes.ui.controller;

import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.service.FichaMedicaService;
import com.controlpacientes.ui.UINavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FichasListController {

    private final FichaMedicaService fichaMedicaService;
    private final UINavigator uiNavigator;

    @FXML
    private TableView<FichaMedica> fichasTable;
    @FXML
    private TableColumn<FichaMedica, String> colFecha;
    @FXML
    private TableColumn<FichaMedica, String> colPaciente;
    @FXML
    private TableColumn<FichaMedica, String> colProfesional;
    @FXML
    private TableColumn<FichaMedica, String> colDiagnostico;
    @FXML
    private TableColumn<FichaMedica, Void> colAcciones;

    private ObservableList<FichaMedica> observableFichas = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        fichasTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        colFecha.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFechaAtencion().format(formatter)));
        colPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaciente() != null ? cellData.getValue().getPaciente().getNombreCompleto() : ""));
        colProfesional
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesionalNombre()));
        colDiagnostico.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiagnostico()));

        addButtonToTable();
        loadAllFichas();
    }

    private void addButtonToTable() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button newBtn = new Button("Nueva Ficha");
            private final Button viewBtn = new Button("Ver/Editar");
            private final Button deleteBtn = new Button("Eliminar");
            private final HBox container = new HBox(10, newBtn, viewBtn, deleteBtn);

            {
                newBtn.getStyleClass().add("btn-sm-success");
                viewBtn.getStyleClass().add("btn-sm-outline");
                deleteBtn.getStyleClass().add("btn-sm-danger");
                newBtn.setOnAction(event -> {
                    FichaMedica f = getTableView().getItems().get(getIndex());
                    handleNewFichaForPaciente(f.getPaciente());
                });
                viewBtn.setOnAction(event -> {
                    FichaMedica f = getTableView().getItems().get(getIndex());
                    handleEditFicha(f);
                });
                deleteBtn.setOnAction(event -> {
                    FichaMedica f = getTableView().getItems().get(getIndex());
                    handleDeleteFicha(f);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadAllFichas() {
        List<FichaMedica> list = fichaMedicaService.findAllOrderByFechaDesc();
        observableFichas.clear();
        observableFichas.addAll(list);
        fichasTable.setItems(observableFichas);
    }

    @FXML
    private void handleNewFicha() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Para crear una nueva ficha, selecciona un paciente desde 'Gestionar Pacientes'.");
        alert.showAndWait();
    }

    private void handleNewFichaForPaciente(com.controlpacientes.model.Paciente paciente) {
        uiNavigator.openModal("/fxml/ficha_form.fxml", "Nueva Ficha Médica",
                (FichaMedicaFormController controller) -> {
                    controller.setPaciente(paciente);
                    controller.setFicha(null);
                });
        loadAllFichas();
    }

    private void handleEditFicha(FichaMedica ficha) {
        uiNavigator.openModal("/fxml/ficha_form.fxml", "Editar Ficha Médica",
                (FichaMedicaFormController controller) -> {
                    controller.setPaciente(ficha.getPaciente());
                    controller.setFicha(ficha);
                });
        loadAllFichas();
    }

    private void handleDeleteFicha(FichaMedica ficha) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar esta ficha médica?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                fichaMedicaService.delete(ficha.getId());
                loadAllFichas();
            }
        });
    }
}
