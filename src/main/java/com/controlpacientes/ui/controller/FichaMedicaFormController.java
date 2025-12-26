package com.controlpacientes.ui.controller;

import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.model.MedicamentoAtencion;
import com.controlpacientes.model.Paciente;
import com.controlpacientes.service.FichaMedicaService;
import com.controlpacientes.service.PacienteService;
import com.controlpacientes.service.PDFGeneratorService;
import com.controlpacientes.util.RutUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.print.PrinterJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class FichaMedicaFormController {

    private final FichaMedicaService fichaMedicaService;
    private final PacienteService pacienteService;
    private final PDFGeneratorService pdfGeneratorService;

    @FXML
    private Text titleText;
    
    // Campos del paciente
    @FXML
    private TextField pacienteRutField;
    @FXML
    private TextField pacienteNombreField;
    @FXML
    private TextField pacienteApellidoField;
    @FXML
    private TextField pacienteEdadField;
    @FXML
    private TextField pacienteEmailField;
    @FXML
    private TextField pacienteTelefonoField;
    @FXML
    private TextField pacienteCiudadField;
    @FXML
    private TextField pacienteDireccionField;
    @FXML
    private TextArea pacienteNotasField;
    
    // Campos de la ficha médica
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
    private TableColumn<MedicamentoAtencion, Void> colMedAcciones;
    @FXML
    private Label medicamentosLabel;
    @FXML
    private Button btnGuardar;

    private Paciente currentPaciente;
    private FichaMedica currentFicha;
    private ObservableList<MedicamentoAtencion> observableMedicamentos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        profesionalField.setText("Emilio Alcaino");
        setupTable();
    }

    private void setupTable() {
        medicamentosTable.setEditable(true);
        medicamentosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colMedNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreMedicamento()));
        colMedNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedNombre.setOnEditCommit(e -> e.getRowValue().setNombreMedicamento(e.getNewValue()));

        colMedAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("x");
            {
                deleteBtn.getStyleClass().add("btn-sm-danger");
                deleteBtn.setStyle("-fx-padding: 5px 8px; -fx-font-size: 14px;");
                deleteBtn.setOnAction(event -> {
                    MedicamentoAtencion m = getTableView().getItems().get(getIndex());
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Deseas eliminar este medicamento?");
                    confirmacion.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            observableMedicamentos.remove(m);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(deleteBtn);
                }
            }
        });

        medicamentosTable.setItems(observableMedicamentos);
        
        // Listener para actualizar el contador
        observableMedicamentos.addListener((javafx.collections.ListChangeListener<MedicamentoAtencion>) c -> {
            actualizarContadorMedicamentos();
        });
    }

    private void actualizarContadorMedicamentos() {
        medicamentosLabel.setText("Medicamentos (" + observableMedicamentos.size() + ")");
    }

    public void setPaciente(Paciente paciente) {
        this.currentPaciente = paciente;
        cargarDatosPaciente();
    }

    private void cargarDatosPaciente() {
        if (currentPaciente != null) {
            pacienteRutField.setText(RutUtils.formatRut(currentPaciente.getRut() != null ? currentPaciente.getRut() : ""));
            pacienteNombreField.setText(currentPaciente.getNombre() != null ? currentPaciente.getNombre() : "");
            pacienteApellidoField.setText(currentPaciente.getApellido() != null ? currentPaciente.getApellido() : "");
            pacienteEdadField.setText(String.valueOf(calcularEdad(currentPaciente.getFechaNacimiento())));
            pacienteEmailField.setText(currentPaciente.getEmail() != null ? currentPaciente.getEmail() : "");
            pacienteTelefonoField.setText(currentPaciente.getTelefono() != null ? currentPaciente.getTelefono() : "");
            pacienteCiudadField.setText(currentPaciente.getCiudad() != null ? currentPaciente.getCiudad() : "");
            pacienteDireccionField.setText(currentPaciente.getDireccion() != null ? currentPaciente.getDireccion() : "");
            pacienteNotasField.setText(currentPaciente.getNotasClinicas() != null ? currentPaciente.getNotasClinicas() : "");
        }
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNacimiento.getYear();
        
        if (hoy.getMonthValue() < fechaNacimiento.getMonthValue() ||
            (hoy.getMonthValue() == fechaNacimiento.getMonthValue() && 
             hoy.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        
        return edad;
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
        MedicamentoAtencion nuevo = MedicamentoAtencion.builder()
                .nombreMedicamento("")
                .build();
        observableMedicamentos.add(nuevo);
        
        // Hacer scroll a la última fila
        medicamentosTable.scrollTo(observableMedicamentos.size() - 1);
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

            // Actualizar todos los datos del paciente desde los campos de la ficha
            if (currentPaciente != null) {
                currentPaciente.setNombre(pacienteNombreField.getText());
                currentPaciente.setApellido(pacienteApellidoField.getText());
                currentPaciente.setEmail(pacienteEmailField.getText());
                currentPaciente.setTelefono(pacienteTelefonoField.getText());
                currentPaciente.setCiudad(pacienteCiudadField.getText());
                currentPaciente.setDireccion(pacienteDireccionField.getText());
                currentPaciente.setNotasClinicas(pacienteNotasField.getText());
                pacienteService.save(currentPaciente);
            }

            fichaMedicaService.save(currentFicha);
            closeWindow();
        } catch (Exception e) {
            mostrarError("Error", "No se pudo guardar la ficha: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleDownloadPDF() {
        if (currentFicha == null || currentFicha.getId() == null) {
            mostrarError("Error", "Debe guardar la ficha antes de descargar PDF");
            return;
        }
        
        try {
            // Obtener ruta por defecto
            String defaultPath = pdfGeneratorService.generateDefaultFilePath(currentFicha);
            
            // Mostrar diálogo de selección de directorio
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Seleccionar carpeta para guardar PDF");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Downloads"));
            
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            File selectedDirectory = directoryChooser.showDialog(stage);
            
            if (selectedDirectory != null) {
                // Crear el nombre del archivo
                String fileName = String.format("Ficha_%s_%s.pdf",
                        currentFicha.getPaciente().getRut().replace("-", ""),
                        System.currentTimeMillis());
                
                String outputPath = selectedDirectory.getAbsolutePath() + File.separator + fileName;
                
                // Generar el PDF
                pdfGeneratorService.generateFichaMedicaPDF(currentFicha, outputPath);
                
                // Mostrar mensaje de éxito
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Éxito");
                alerta.setHeaderText(null);
                alerta.setContentText("PDF generado correctamente en:\n" + outputPath);
                alerta.showAndWait();
            }
        } catch (Exception e) {
            mostrarError("Error", "No se pudo descargar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrint() {
        try {
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            
            if (printerJob != null) {
                // Mostrar diálogo de selección de impresora
                if (printerJob.showPrintDialog(stage)) {
                    // Crear un VBox con el contenido a imprimir
                    VBox printContent = crearContenidoImpresion();
                    
                    // Imprimir el contenido
                    boolean success = printerJob.printPage(printContent);
                    if (success) {
                        printerJob.endJob();
                        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                        alerta.setTitle("Éxito");
                        alerta.setHeaderText(null);
                        alerta.setContentText("Ficha enviada a la impresora correctamente");
                        alerta.showAndWait();
                    }
                }
            }
        } catch (Exception e) {
            mostrarError("Error", "No se pudo imprimir la ficha: " + e.getMessage());
        }
    }

    private VBox crearContenidoImpresion() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20; -fx-font-family: 'Arial'; -fx-font-size: 11;");
        
        // Título
        Label titulo = new Label("FICHA MÉDICA");
        titulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        content.getChildren().add(titulo);
        
        // Información del paciente
        Label pacienteLabel = new Label("INFORMACIÓN DEL PACIENTE");
        pacienteLabel.setStyle("-fx-font-weight: bold; -fx-underline: true;");
        content.getChildren().add(pacienteLabel);
        
        content.getChildren().add(new Label("RUT: " + pacienteRutField.getText()));
        content.getChildren().add(new Label("Nombre: " + pacienteNombreField.getText() + " " + pacienteApellidoField.getText()));
        content.getChildren().add(new Label("Edad: " + pacienteEdadField.getText()));
        content.getChildren().add(new Label("Email: " + pacienteEmailField.getText()));
        content.getChildren().add(new Label("Teléfono: " + pacienteTelefonoField.getText()));
        
        // Información de la ficha
        Label fichaLabel = new Label("INFORMACIÓN DE LA FICHA");
        fichaLabel.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-padding: 10 0 0 0;");
        content.getChildren().add(fichaLabel);
        
        content.getChildren().add(new Label("Fecha: " + fechaAtencionPicker.getValue()));
        content.getChildren().add(new Label("Profesional: " + profesionalField.getText()));
        
        // Motivo
        Label motivoLabel = new Label("MOTIVO DE LA CONSULTA:");
        motivoLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        content.getChildren().add(motivoLabel);
        content.getChildren().add(new Label(motivoField.getText()));
        
        // Diagnóstico
        Label diagLabel = new Label("DIAGNÓSTICO:");
        diagLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        content.getChildren().add(diagLabel);
        content.getChildren().add(new Label(diagnosticoField.getText()));
        
        // Medicamentos
        if (!observableMedicamentos.isEmpty()) {
            Label medLabel = new Label("MEDICAMENTOS:");
            medLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
            content.getChildren().add(medLabel);
            
            for (MedicamentoAtencion med : observableMedicamentos) {
                content.getChildren().add(new Label("• " + med.getNombreMedicamento()));
            }
        }
        
        return content;
    }

    private String generarContenidoHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Ficha Médica</h1>");
        html.append("<h2>Información del Paciente</h2>");
        html.append("<p>RUT: ").append(pacienteRutField.getText()).append("</p>");
        html.append("<p>Nombre: ").append(pacienteNombreField.getText()).append(" ").append(pacienteApellidoField.getText()).append("</p>");
        html.append("<p>Edad: ").append(pacienteEdadField.getText()).append("</p>");
        html.append("<p>Email: ").append(pacienteEmailField.getText()).append("</p>");
        html.append("<p>Teléfono: ").append(pacienteTelefonoField.getText()).append("</p>");
        html.append("<p>Ciudad: ").append(pacienteCiudadField.getText()).append("</p>");
        html.append("<p>Dirección: ").append(pacienteDireccionField.getText()).append("</p>");
        html.append("<h2>Información de la Ficha</h2>");
        html.append("<p>Fecha: ").append(fechaAtencionPicker.getValue()).append("</p>");
        html.append("<p>Profesional: ").append(profesionalField.getText()).append("</p>");
        html.append("<h3>Motivo de la Consulta</h3>");
        html.append("<p>").append(motivoField.getText()).append("</p>");
        html.append("<h3>Diagnóstico</h3>");
        html.append("<p>").append(diagnosticoField.getText()).append("</p>");
        if (!observableMedicamentos.isEmpty()) {
            html.append("<h3>Medicamentos</h3>");
            html.append("<ul>");
            for (MedicamentoAtencion med : observableMedicamentos) {
                html.append("<li>").append(med.getNombreMedicamento()).append("</li>");
            }
            html.append("</ul>");
        }
        html.append("</body></html>");
        return html.toString();
    }

    private void closeWindow() {
        ((Stage) btnGuardar.getScene().getWindow()).close();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
