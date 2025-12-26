package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.service.PacienteService;
import com.controlpacientes.service.FichaMedicaService;
import com.controlpacientes.ui.UINavigator;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MainController {

    private final UINavigator uiNavigator;
    private final PacienteService pacienteService;
    private final FichaMedicaService fichaMedicaService;

    @FXML
    private VBox dynamicContentContainer;

    @FXML
    public void initialize() {
        loadPacientesView();
    }

    @FXML
    private void handleShowPacientes() {
        loadPacientesView();
    }

    @FXML
    private void handleShowFichas() {
        loadFichasView();
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) dynamicContentContainer.getScene().getWindow();
        stage.close();
    }

    private void loadPacientesView() {
        Parent content = uiNavigator.loadView("/fxml/pacientes_list.fxml", (PacientesListController controller) -> {
            // El controlador se inicializa automáticamente
        });
        if (content != null) {
            dynamicContentContainer.getChildren().clear();
            dynamicContentContainer.getChildren().add(content);
        }
    }

    private void loadFichasView() {
        Parent content = uiNavigator.loadView("/fxml/fichas_main.fxml", (FichasListController controller) -> {
            // El controlador se inicializa automáticamente
        });
        if (content != null) {
            dynamicContentContainer.getChildren().clear();
            dynamicContentContainer.getChildren().add(content);
        }
    }

    @FXML
    private void handleExportarExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
            fileChooser.setInitialFileName("control_pacientes_" + System.currentTimeMillis() + ".xlsx");

            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                exportarDatos(file);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos exportados correctamente a: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al exportar: " + e.getMessage());
        }
    }

    private void exportarDatos(File file) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Hoja de Pacientes
        Sheet hojaPacientes = workbook.createSheet("Pacientes");
        crearHojaPacientes(hojaPacientes);

        // Hoja de Fichas Médicas
        Sheet hojaFichas = workbook.createSheet("Fichas Médicas");
        crearHojaFichas(hojaFichas);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private void crearHojaPacientes(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("RUT");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Apellido");
        headerRow.createCell(3).setCellValue("Fecha Nacimiento");
        headerRow.createCell(4).setCellValue("Email");
        headerRow.createCell(5).setCellValue("Teléfono");
        headerRow.createCell(6).setCellValue("Ciudad");
        headerRow.createCell(7).setCellValue("Dirección");

        List<Paciente> pacientes = pacienteService.findAll();
        int rowNum = 1;
        for (Paciente p : pacientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getRut());
            row.createCell(1).setCellValue(p.getNombre());
            row.createCell(2).setCellValue(p.getApellido());
            row.createCell(3).setCellValue(p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "");
            row.createCell(4).setCellValue(p.getEmail() != null ? p.getEmail() : "");
            row.createCell(5).setCellValue(p.getTelefono() != null ? p.getTelefono() : "");
            row.createCell(6).setCellValue(p.getCiudad() != null ? p.getCiudad() : "");
            row.createCell(7).setCellValue(p.getDireccion() != null ? p.getDireccion() : "");
        }

        // Auto-ajustar ancho de columnas
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaFichas(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("RUT Paciente");
        headerRow.createCell(1).setCellValue("Nombre Paciente");
        headerRow.createCell(2).setCellValue("Fecha Atención");
        headerRow.createCell(3).setCellValue("Profesional");
        headerRow.createCell(4).setCellValue("Motivo Consulta");
        headerRow.createCell(5).setCellValue("Diagnóstico");
        headerRow.createCell(6).setCellValue("Tratamiento");

        List<FichaMedica> fichas = fichaMedicaService.findAll();
        int rowNum = 1;
        for (FichaMedica f : fichas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(f.getPaciente().getRut());
            row.createCell(1).setCellValue(f.getPaciente().getNombreCompleto());
            row.createCell(2).setCellValue(f.getFechaAtencion() != null ? f.getFechaAtencion().toString() : "");
            row.createCell(3).setCellValue(f.getProfesionalNombre() != null ? f.getProfesionalNombre() : "");
            row.createCell(4).setCellValue(f.getMotivoConsulta() != null ? f.getMotivoConsulta() : "");
            row.createCell(5).setCellValue(f.getDiagnostico() != null ? f.getDiagnostico() : "");
            row.createCell(6).setCellValue(f.getTratamiento() != null ? f.getTratamiento() : "");
        }

        // Auto-ajustar ancho de columnas
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Control de Pacientes");
        alert.setContentText("Sistema de Control de Pacientes\n\n" +
                "Versión: 1.0.0\n" +
                "Desarrollado por: Héctor Martínez\n" +
                "Año: 2025\n\n" +
                "Este sistema permite gestionar pacientes y sus fichas médicas de forma eficiente.");
        alert.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
