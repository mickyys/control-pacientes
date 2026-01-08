package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.model.MedicamentoAtencion;
import com.controlpacientes.service.PacienteService;
import com.controlpacientes.service.FichaMedicaService;
import com.controlpacientes.service.MedicamentoAtencionService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MainController {

    private final PacienteService pacienteService;
    private final FichaMedicaService fichaMedicaService;
    private final MedicamentoAtencionService medicamentoService;

    // Paciente Information Controls (Left Column - Top)
    @FXML private TextField tfPacienteRut;
    @FXML private TextField tfPacienteNombre;
    @FXML private TextField tfPacienteApellido;
    @FXML private TextField tfPacienteEmail;
    @FXML private TextField tfPacienteTelefono;
    @FXML private TextField tfPacienteCiudad;
    @FXML private TextField tfPacienteDireccion;
    @FXML private Button btnPacienteNuevo;
    @FXML private Button btnPacienteEditar;
    @FXML private Button btnPacienteEliminar;

    // Medical Records Controls (Left Column - Bottom)
    @FXML private ComboBox<String> cbFichaFechas;
    @FXML private TextArea taMotivoconsulta;
    @FXML private TextArea taSintesisDiagnostica;
    @FXML private Button btnFichaLimpiar;
    @FXML private Button btnFichaGuardar;

    // Medications Controls (Center Column)
    @FXML private VBox vbMedicamentos;

    // Search Controls (Right Column - Top)
    @FXML private TextField tfBuscaRut;
    @FXML private TextField tfBuscaNombre;
    @FXML private TextField tfBuscaCiudad;
    @FXML private TextField tfBuscaAno;
    @FXML private Button btnBuscar;
    @FXML private Label lblResultados;

    // Patients List (Right Column - Bottom)
    @FXML private TableView<Paciente> tvPacientes;
    @FXML private TableColumn<Paciente, String> tcRut;
    @FXML private TableColumn<Paciente, String> tcNombre;
    @FXML private TableColumn<Paciente, String> tcApellido;
    @FXML private TableColumn<Paciente, String> tcCiudad;
    @FXML private TableColumn<Paciente, String> tcUltimaVisita;
    @FXML private TableColumn<Paciente, Void> tcEliminar;

    private Paciente pacienteSeleccionado;
    private FichaMedica fichaSeleccionada;
    private List<MedicamentoAtencion> medicamentosActuales = new ArrayList<>();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupListeners();
        cargarPacientes();
        limpiarFormularioPaciente();
        crearCamposMedicamentos();
    }

    private void setupTableColumns() {
        tcRut.setCellValueFactory(cellData -> {
            String rut = cellData.getValue().getRut();
            return new javafx.beans.property.SimpleStringProperty(formatearRut(rut));
        });
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        tcCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        
        tcUltimaVisita.setCellValueFactory(cellData -> {
            Paciente paciente = cellData.getValue();
            String ultimaVisita = obtenerUltimaVisita(paciente);
            return new javafx.beans.property.SimpleStringProperty(ultimaVisita);
        });

        // Configurar columna de eliminar con botones
        tcEliminar.setCellFactory(column -> new javafx.scene.control.TableCell<Paciente, Void>() {
            private final Button btn = new Button("Eliminar");

            {
                btn.setStyle("-fx-text-fill: black; -fx-padding: 5;");
                btn.setOnAction(event -> {
                    Paciente paciente = getTableView().getItems().get(getIndex());
                    mostrarConfirmacionEliminar(paciente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private String formatearRut(String rut) {
        if (rut == null || rut.isEmpty()) {
            return "";
        }
        
        // Eliminar puntos y guiones para procesar
        String rutLimpio = rut.replaceAll("[^0-9Kk]", "");
        
        if (rutLimpio.length() < 2) {
            return rut;
        }
        
        // Formato: XX.XXX.XXX-X (ej: 12.345.678-9)
        String digitos = rutLimpio.substring(0, rutLimpio.length() - 1);
        String verificador = rutLimpio.substring(rutLimpio.length() - 1);
        
        // Agregar puntos cada 3 dígitos de derecha a izquierda
        StringBuilder rutFormateado = new StringBuilder();
        int contador = 0;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            if (contador == 3) {
                rutFormateado.insert(0, ".");
                contador = 0;
            }
            rutFormateado.insert(0, digitos.charAt(i));
            contador++;
        }
        
        return rutFormateado.toString() + "-" + verificador;
    }

    private String obtenerUltimaVisita(Paciente paciente) {
        List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(paciente.getId());
        if (fichas.isEmpty()) {
            return "Sin registros";
        }
        java.time.LocalDateTime ultimaFecha = fichas.stream()
                .map(FichaMedica::getFechaAtencion)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
        
        if (ultimaFecha == null) {
            return "Sin registros";
        }
        
        // Formato: dd/MM/yyyy HH:mm
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return ultimaFecha.format(formatter);
    }

    private void setupListeners() {
        tvPacientes.setOnMouseClicked(event -> {
            Paciente seleccionado = tvPacientes.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                cargarPacienteEnFormulario(seleccionado);
            }
        });

        cbFichaFechas.setOnAction(e -> {
            String fechaFormateada = cbFichaFechas.getSelectionModel().getSelectedItem();
            if (fechaFormateada != null && pacienteSeleccionado != null) {
                if (fechaFormateada.contains("Nueva")) {
                    crearFichaMedicaNueva();
                } else {
                    cargarFichaMedicaPorFecha(pacienteSeleccionado, fechaFormateada);
                }
            }
        });
    }

    private void cargarPacientes() {
        List<Paciente> pacientes = pacienteService.findAll();
        // Ordenar alfabéticamente por nombre
        pacientes.sort(Comparator.comparing(p -> p.getNombre() != null ? p.getNombre() : ""));
        tvPacientes.getItems().setAll(pacientes);
    }

    @FXML
    private void handleBuscar() {
        String rut = tfBuscaRut.getText().trim();
        String nombre = tfBuscaNombre.getText().trim();
        String ciudad = tfBuscaCiudad.getText().trim();
        String ano = tfBuscaAno.getText().trim();

        List<Paciente> resultados = pacienteService.findAll().stream()
                .filter(p -> rut.isEmpty() || p.getRut().contains(rut))
                .filter(p -> nombre.isEmpty() || 
                    p.getNombre().toLowerCase().contains(nombre.toLowerCase()) ||
                    (p.getApellido() != null && p.getApellido().toLowerCase().contains(nombre.toLowerCase())))
                .filter(p -> ciudad.isEmpty() || (p.getCiudad() != null && p.getCiudad().toLowerCase().contains(ciudad.toLowerCase())))
                .filter(p -> ano.isEmpty() || verificarAnoUltimaVisita(p, ano))
                .sorted(Comparator.comparing(p -> p.getNombre() != null ? p.getNombre() : ""))
                .collect(Collectors.toList());

        tvPacientes.getItems().setAll(resultados);
        lblResultados.setText(String.valueOf(resultados.size()));
    }

    private boolean verificarAnoUltimaVisita(Paciente paciente, String anoTexto) {
        try {
            int anoFiltro = Integer.parseInt(anoTexto);
            List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(paciente.getId());
            
            if (fichas.isEmpty()) {
                return false;
            }
            
            java.time.LocalDateTime ultimaFecha = fichas.stream()
                    .map(FichaMedica::getFechaAtencion)
                    .max(java.time.LocalDateTime::compareTo)
                    .orElse(null);
            
            return ultimaFecha != null && ultimaFecha.getYear() == anoFiltro;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleBuscadorLimpiar() {
        tfBuscaRut.clear();
        tfBuscaNombre.clear();
        tfBuscaCiudad.clear();
        tfBuscaAno.clear();
        cargarPacientes();
        lblResultados.setText("0");
    }

    @FXML
    private void handlePacienteNuevo() {
        limpiarFormularioPaciente();
        pacienteSeleccionado = null;
        habilitarCamposPaciente(true);
        tfPacienteRut.requestFocus();
    }

    @FXML
    private void handlePacienteEditar() {
        if (pacienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un paciente para editar");
            return;
        }
        habilitarCamposPaciente(true);
        tfPacienteRut.requestFocus();
    }

    @FXML
    private void handlePacienteEliminar() {
        if (pacienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un paciente para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar paciente");
        confirmacion.setContentText("¿Está seguro de que desea eliminar a " + pacienteSeleccionado.getNombre() + "?");
        
        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                pacienteService.delete(pacienteSeleccionado.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Paciente eliminado correctamente");
                limpiarFormularioPaciente();
                cargarPacientes();
                tvPacientes.getSelectionModel().clearSelection();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handlePacienteGuardar() {
        try {
            if (tfPacienteRut.getText().trim().isEmpty() || tfPacienteNombre.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "RUT y Nombre son requeridos");
                return;
            }

            Paciente paciente = pacienteSeleccionado != null ? pacienteSeleccionado : new Paciente();
            paciente.setRut(tfPacienteRut.getText().trim());
            paciente.setNombre(tfPacienteNombre.getText().trim());
            paciente.setApellido(tfPacienteApellido.getText().trim());
            paciente.setEmail(tfPacienteEmail.getText().trim());
            paciente.setTelefono(tfPacienteTelefono.getText().trim());
            paciente.setCiudad(tfPacienteCiudad.getText().trim());
            paciente.setDireccion(tfPacienteDireccion.getText().trim());

            pacienteService.save(paciente);
            pacienteSeleccionado = paciente;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Paciente guardado correctamente");
            habilitarCamposPaciente(false);
            cargarPacientes();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleFichaLimpiar() {
        taMotivoconsulta.clear();
        taSintesisDiagnostica.clear();
        limpiarMedicamentos();
    }

    @FXML
    private void handleFichaGuardar() {
        try {
            // Validar que RUT y Nombre sean obligatorios
            if (tfPacienteRut.getText().trim().isEmpty() || tfPacienteNombre.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "RUT y Nombre del paciente son requeridos");
                return;
            }

            // Crear paciente si no existe
            if (pacienteSeleccionado == null) {
                pacienteSeleccionado = new Paciente();
            }

            // Guardar información del paciente
            pacienteSeleccionado.setRut(tfPacienteRut.getText().trim());
            pacienteSeleccionado.setNombre(tfPacienteNombre.getText().trim());
            pacienteSeleccionado.setApellido(tfPacienteApellido.getText().trim());
            pacienteSeleccionado.setEmail(tfPacienteEmail.getText().trim());
            pacienteSeleccionado.setTelefono(tfPacienteTelefono.getText().trim());
            pacienteSeleccionado.setCiudad(tfPacienteCiudad.getText().trim());
            pacienteSeleccionado.setDireccion(tfPacienteDireccion.getText().trim());
            
            Paciente pacienteGuardado = pacienteService.save(pacienteSeleccionado);
            pacienteSeleccionado = pacienteGuardado;

            // Crear o editar ficha médica
            FichaMedica ficha;
            if (fichaSeleccionada != null && fichaSeleccionada.getId() != null) {
                // Editar ficha existente
                ficha = fichaSeleccionada;
            } else {
                // Crear nueva ficha
                ficha = new FichaMedica();
                ficha.setPaciente(pacienteGuardado);
                ficha.setFechaAtencion(LocalDateTime.now());
            }
            
            ficha.setMotivoConsulta(taMotivoconsulta.getText());
            ficha.setDiagnostico(taSintesisDiagnostica.getText());

            FichaMedica fichaguardada = fichaMedicaService.save(ficha);
            fichaSeleccionada = fichaguardada;

            // Guardar medicamentos desde los TextFields
            // Primero eliminar medicamentos existentes si es edición
            if (fichaguardada.getId() != null && !medicamentosActuales.isEmpty()) {
                for (MedicamentoAtencion med : medicamentosActuales) {
                    if (med.getId() != null) {
                        medicamentoService.delete(med.getId());
                    }
                }
                medicamentosActuales.clear();
            }

            // Guardar nuevos medicamentos
            int indice = 0;
            for (javafx.scene.Node node : vbMedicamentos.getChildren()) {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    if (row.getChildren().size() > 1 && row.getChildren().get(1) instanceof TextField) {
                        TextField tf = (TextField) row.getChildren().get(1);
                        String nombreMedicamento = tf.getText().trim();
                        if (!nombreMedicamento.isEmpty()) {
                            MedicamentoAtencion med = new MedicamentoAtencion();
                            med.setFichaMedica(fichaguardada);
                            med.setNombreMedicamento(nombreMedicamento);
                            medicamentoService.save(med);
                        }
                    }
                }
                indice++;
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Paciente y ficha médica guardados correctamente");
            habilitarCamposPaciente(false);
            cargarFichasDelPaciente();
            cargarPacientes();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
    }

    private void cargarPacienteEnFormulario(Paciente paciente) {
        pacienteSeleccionado = paciente;
        tfPacienteRut.setText(paciente.getRut());
        tfPacienteNombre.setText(paciente.getNombre());
        tfPacienteApellido.setText(paciente.getApellido() != null ? paciente.getApellido() : "");
        tfPacienteEmail.setText(paciente.getEmail() != null ? paciente.getEmail() : "");
        tfPacienteTelefono.setText(paciente.getTelefono() != null ? paciente.getTelefono() : "");
        tfPacienteCiudad.setText(paciente.getCiudad() != null ? paciente.getCiudad() : "");
        tfPacienteDireccion.setText(paciente.getDireccion() != null ? paciente.getDireccion() : "");

        // Los campos son editables al seleccionar un paciente
        habilitarCamposPaciente(true);
        cargarFichasDelPaciente();
    }

    private void cargarFichasDelPaciente() {
        if (pacienteSeleccionado != null) {
            List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(pacienteSeleccionado.getId());
            
            // Formatear fechas a formato dd/MM/yyyy
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<String> fechasFormateadas = fichas.stream()
                    .map(FichaMedica::getFechaAtencion)
                    .sorted(Comparator.reverseOrder())
                    .map(fecha -> fecha.format(formatter))
                    .collect(Collectors.toList());
            
            // Agregar la fecha de hoy al principio para crear una nueva ficha
            String fechaHoy = java.time.LocalDate.now().format(formatter);
            fechasFormateadas.add(0, fechaHoy + " (Nueva)");
            
            cbFichaFechas.getItems().setAll(fechasFormateadas);
            
            // Seleccionar la fecha de hoy (nueva ficha) por defecto
            cbFichaFechas.getSelectionModel().select(0);
            crearFichaMedicaNueva();
        }
    }

    private void crearFichaMedicaNueva() {
        fichaSeleccionada = null;
        taMotivoconsulta.clear();
        taSintesisDiagnostica.clear();
        limpiarMedicamentos();
    }

    private void cargarFichaMedica(Paciente paciente, LocalDateTime fecha) {
        List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(paciente.getId())
                .stream()
                .filter(f -> f.getFechaAtencion().equals(fecha))
                .collect(Collectors.toList());

        if (!fichas.isEmpty()) {
            fichaSeleccionada = fichas.get(0);
            taMotivoconsulta.setText(fichaSeleccionada.getMotivoConsulta() != null ? fichaSeleccionada.getMotivoConsulta() : "");
            taSintesisDiagnostica.setText(fichaSeleccionada.getDiagnostico() != null ? fichaSeleccionada.getDiagnostico() : "");

            // Cargar medicamentos
            List<MedicamentoAtencion> medicamentos = medicamentoService.findByFichaMedica(fichaSeleccionada);
            cargarMedicamentosEnUI(medicamentos);
        }
    }

    private void cargarFichaMedicaPorFecha(Paciente paciente, String fechaFormateada) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.LocalDate fechaBuscada = java.time.LocalDate.parse(fechaFormateada, formatter);
        
        List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(paciente.getId())
                .stream()
                .filter(f -> f.getFechaAtencion().toLocalDate().equals(fechaBuscada))
                .sorted(Comparator.comparing(FichaMedica::getFechaAtencion).reversed())
                .collect(Collectors.toList());

        if (!fichas.isEmpty()) {
            fichaSeleccionada = fichas.get(0);
            taMotivoconsulta.setText(fichaSeleccionada.getMotivoConsulta() != null ? fichaSeleccionada.getMotivoConsulta() : "");
            taSintesisDiagnostica.setText(fichaSeleccionada.getDiagnostico() != null ? fichaSeleccionada.getDiagnostico() : "");

            // Cargar medicamentos
            List<MedicamentoAtencion> medicamentos = medicamentoService.findByFichaMedica(fichaSeleccionada);
            cargarMedicamentosEnUI(medicamentos);
        }
    }

    private void crearCamposMedicamentos() {
        for (int i = 1; i <= 10; i++) {
            HBox row = new HBox(10);
            row.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5;");

            Label label = new Label(String.valueOf(i) + ".");
            label.setPrefWidth(30);

            TextField tf = new TextField();
            tf.setPromptText("Medicamento #" + i);
            tf.getStyleClass().add("input-field");
            tf.setPrefWidth(200);

            row.getChildren().addAll(label, tf);
            vbMedicamentos.getChildren().add(row);
        }
    }

    private void cargarMedicamentosEnUI(List<MedicamentoAtencion> medicamentos) {
        medicamentosActuales.clear();

        // Limpiar campos
        for (javafx.scene.Node node : vbMedicamentos.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                if (row.getChildren().size() > 1 && row.getChildren().get(1) instanceof TextField) {
                    ((TextField) row.getChildren().get(1)).clear();
                }
            }
        }

        // Cargar medicamentos
        for (int i = 0; i < medicamentos.size() && i < 10; i++) {
            MedicamentoAtencion med = medicamentos.get(i);
            medicamentosActuales.add(med);

            HBox row = (HBox) vbMedicamentos.getChildren().get(i);
            if (row.getChildren().size() > 1 && row.getChildren().get(1) instanceof TextField) {
                ((TextField) row.getChildren().get(1)).setText(med.getNombreMedicamento() != null ? med.getNombreMedicamento() : "");
            }
        }
    }

    private void limpiarMedicamentos() {
        medicamentosActuales.clear();
        for (javafx.scene.Node node : vbMedicamentos.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                if (row.getChildren().size() > 1 && row.getChildren().get(1) instanceof TextField) {
                    ((TextField) row.getChildren().get(1)).clear();
                }
            }
        }
    }

    private void limpiarFormularioPaciente() {
        tfPacienteRut.clear();
        tfPacienteNombre.clear();
        tfPacienteApellido.clear();
        tfPacienteEmail.clear();
        tfPacienteTelefono.clear();
        tfPacienteCiudad.clear();
        tfPacienteDireccion.clear();
        cbFichaFechas.getItems().clear();
        taMotivoconsulta.clear();
        taSintesisDiagnostica.clear();
        pacienteSeleccionado = null;
        fichaSeleccionada = null;
        limpiarMedicamentos();
        habilitarCamposPaciente(false);
    }

    private void habilitarCamposPaciente(boolean habilitar) {
        tfPacienteRut.setDisable(!habilitar);
        tfPacienteNombre.setDisable(!habilitar);
        tfPacienteApellido.setDisable(!habilitar);
        tfPacienteEmail.setDisable(!habilitar);
        tfPacienteTelefono.setDisable(!habilitar);
        tfPacienteCiudad.setDisable(!habilitar);
        tfPacienteDireccion.setDisable(!habilitar);
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

        Sheet hojaPacientes = workbook.createSheet("Pacientes");
        crearHojaPacientes(hojaPacientes);

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
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Teléfono");
        headerRow.createCell(5).setCellValue("Ciudad");
        headerRow.createCell(6).setCellValue("Dirección");

        List<Paciente> pacientes = pacienteService.findAll();
        int rowNum = 1;
        for (Paciente p : pacientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getRut());
            row.createCell(1).setCellValue(p.getNombre());
            row.createCell(2).setCellValue(p.getApellido() != null ? p.getApellido() : "");
            row.createCell(3).setCellValue(p.getEmail() != null ? p.getEmail() : "");
            row.createCell(4).setCellValue(p.getTelefono() != null ? p.getTelefono() : "");
            row.createCell(5).setCellValue(p.getCiudad() != null ? p.getCiudad() : "");
            row.createCell(6).setCellValue(p.getDireccion() != null ? p.getDireccion() : "");
        }

        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaFichas(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("RUT Paciente");
        headerRow.createCell(1).setCellValue("Nombre Paciente");
        headerRow.createCell(2).setCellValue("Fecha Atención");
        headerRow.createCell(3).setCellValue("Motivo Consulta");
        headerRow.createCell(4).setCellValue("Diagnóstico");

        List<FichaMedica> fichas = fichaMedicaService.findAll();
        int rowNum = 1;
        for (FichaMedica f : fichas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(f.getPaciente().getRut());
            row.createCell(1).setCellValue(f.getPaciente().getNombreCompleto());
            row.createCell(2).setCellValue(f.getFechaAtencion() != null ? f.getFechaAtencion().toString() : "");
            row.createCell(3).setCellValue(f.getMotivoConsulta() != null ? f.getMotivoConsulta() : "");
            row.createCell(4).setCellValue(f.getDiagnostico() != null ? f.getDiagnostico() : "");
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Control de Pacientes");
        alert.setContentText("Sistema de Control de Pacientes\n\n" +
                "Versión: 2.0.0\n" +
                "Interfaz centralizada\n\n" +
                "Gestión eficiente de pacientes y fichas médicas.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void mostrarConfirmacionEliminar(Paciente paciente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar paciente?");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + paciente.getNombre() + " " + 
                (paciente.getApellido() != null ? paciente.getApellido() : "") + "?");

        java.util.Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                pacienteService.delete(paciente.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Paciente eliminado correctamente");
                limpiarFormularioPaciente();
                cargarPacientes();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
