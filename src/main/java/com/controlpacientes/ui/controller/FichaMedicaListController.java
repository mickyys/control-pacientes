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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML
    private Button btnNewFicha;
    @FXML
    private TextField searchFecha;
    @FXML
    private TextField searchPaciente;
    @FXML
    private TextField searchDiagnostico;

    private Paciente currentPaciente;
    private ObservableList<FichaMedica> observableFichas = FXCollections.observableArrayList();
    private List<FichaMedica> allFichas = List.of();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter searchFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
        allFichas = fichaMedicaService.findAllOrderByFechaDesc();
        observableFichas.clear();
        observableFichas.addAll(allFichas);
        clearSearchFilters();
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
            allFichas = fichaMedicaService.findByPacienteId(currentPaciente.getId());
            observableFichas.clear();
            observableFichas.addAll(allFichas);
            clearSearchFilters();
        }
    }

    private void addButtonToTable() {
        colAcciones.setCellFactory(param -> new TableCell<>() {            
            private final Button viewBtn = new Button("Ver/Editar");
            private final Button deleteBtn = new Button("Eliminar");
            private final HBox container = new HBox(10, viewBtn, deleteBtn);

            {                
                viewBtn.getStyleClass().add("btn-sm-outline");
                deleteBtn.getStyleClass().add("btn-sm-danger");
                                
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
        if (currentPaciente == null) {
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

    @FXML
    private void handleSearch() {
        String fechaStr = searchFecha.getText().trim();
        String pacienteStr = searchPaciente.getText().trim().toLowerCase();
        String diagnosticoStr = searchDiagnostico.getText().trim().toLowerCase();

        List<FichaMedica> filtered = allFichas.stream()
                .filter(ficha -> {
                    // Filtrar por fecha
                    if (!fechaStr.isEmpty()) {
                        try {
                            LocalDateTime searchDate = searchFormatter.parse(fechaStr, LocalDateTime::from);
                            
                            // Comparar solo la fecha (sin la hora)
                            if (!ficha.getFechaAtencion().toLocalDate().equals(searchDate.toLocalDate())) {
                                return false;
                            }
                        } catch (DateTimeParseException e) {
                            return false;
                        }
                    }

                    // Filtrar por nombre del paciente
                    if (!pacienteStr.isEmpty()) {
                        String nombrePaciente = ficha.getPaciente() != null ? 
                            ficha.getPaciente().getNombreCompleto().toLowerCase() : "";
                        if (!nombrePaciente.contains(pacienteStr)) {
                            return false;
                        }
                    }

                    // Filtrar por diagnóstico
                    if (!diagnosticoStr.isEmpty()) {
                        String diagnostico = ficha.getDiagnostico() != null ? 
                            ficha.getDiagnostico().toLowerCase() : "";
                        if (!diagnostico.contains(diagnosticoStr)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        observableFichas.clear();
        observableFichas.addAll(filtered);
    }

    @FXML
    private void handleClearSearch() {
        clearSearchFilters();
        observableFichas.clear();
        observableFichas.addAll(allFichas);
    }

    private void clearSearchFilters() {
        searchFecha.clear();
        searchPaciente.clear();
        searchDiagnostico.clear();
    }
}
