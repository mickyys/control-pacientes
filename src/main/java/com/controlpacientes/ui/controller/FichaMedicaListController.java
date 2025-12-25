package com.controlpacientes.ui.controller;

import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.model.Paciente;
import com.controlpacientes.service.FichaMedicaService;
import com.controlpacientes.ui.UINavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FichaMedicaListController {

    private final FichaMedicaService fichaMedicaService;
    private final UINavigator uiNavigator;

    @FXML
    private Text pacienteNameText;
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

    private Paciente currentPaciente;
    private ObservableList<FichaMedica> observableFichas = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean showingAllFichas = false;

    public void setPaciente(Paciente paciente) {
        this.currentPaciente = paciente;
        this.showingAllFichas = false;
        pacienteNameText.setText("Paciente: " + paciente.getNombreCompleto() + " (" + paciente.getRut() + ")");
        loadFichas();
    }

    public void loadAllFichas() {
        this.currentPaciente = null;
        this.showingAllFichas = true;
        pacienteNameText.setText("Todas las Fichas Médicas");
        List<FichaMedica> list = fichaMedicaService.findAllOrderByFechaDesc();
        observableFichas.clear();
        observableFichas.addAll(list);
    }

    @FXML
    public void initialize() {
        // Initialize table items early
        fichasTable.setItems(observableFichas);
        fichasTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        colFecha.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFechaAtencion().format(formatter)));
        colPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaciente() != null ? cellData.getValue().getPaciente().getNombreCompleto() : ""));
        colProfesional
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMotivoConsulta()));
        colDiagnostico.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiagnostico()));

        // Set default text
        if (pacienteNameText != null) {
            pacienteNameText.setText("");
        }
        
        addButtonToTable();
    }

    private void loadFichas() {
        if (currentPaciente != null) {
            List<FichaMedica> list = fichaMedicaService.findByPacienteId(currentPaciente.getId());
            observableFichas.clear();
            observableFichas.addAll(list);
        }
    }

    private void addButtonToTable() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button newFichaBtn = new Button("+ Nueva");
            private final Button viewBtn = new Button("Ver/Editar");
            private final Button deleteBtn = new Button("Eliminar");
            private final HBox container = new HBox(10, newFichaBtn, viewBtn, deleteBtn);

            {
                newFichaBtn.getStyleClass().add("btn-sm-primary");
                viewBtn.getStyleClass().add("btn-sm-outline");
                deleteBtn.getStyleClass().add("btn-sm-danger");
                
                newFichaBtn.setOnAction(event -> {
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

    @FXML
    private void handleNewFicha() {
        if (showingAllFichas) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Para crear una nueva ficha, selecciona un paciente.");
            alert.showAndWait();
            return;
        }
        uiNavigator.openModal("/fxml/ficha_form.fxml", "Nueva Ficha Médica", (FichaMedicaFormController controller) -> {
            controller.setPaciente(currentPaciente);
            controller.setFicha(new FichaMedica());
        });
        loadFichas();
    }

    private void handleNewFichaForPaciente(Paciente paciente) {
        uiNavigator.openModal("/fxml/ficha_form.fxml", "Nueva Ficha Médica", (FichaMedicaFormController controller) -> {
            controller.setPaciente(paciente);
            controller.setFicha(new FichaMedica());
        });
        if (showingAllFichas) {
            loadAllFichas();
        } else {
            loadFichas();
        }
    }

    private void handleEditFicha(FichaMedica ficha) {
        Paciente paciente = ficha.getPaciente();
        uiNavigator.openModal("/fxml/ficha_form.fxml", "Editar Ficha Médica",
                (FichaMedicaFormController controller) -> {
                    controller.setPaciente(paciente);
                    controller.setFicha(ficha);
                });
        if (showingAllFichas) {
            loadAllFichas();
        } else {
            loadFichas();
        }
    }

    private void handleDeleteFicha(FichaMedica ficha) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar esta ficha médica?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                fichaMedicaService.delete(ficha.getId());
                if (showingAllFichas) {
                    loadAllFichas();
                } else {
                    loadFichas();
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        ((Stage) fichasTable.getScene().getWindow()).close();
    }
}
