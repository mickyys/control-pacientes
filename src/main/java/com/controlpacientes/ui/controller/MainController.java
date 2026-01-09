package com.controlpacientes.ui.controller;

import com.controlpacientes.model.Paciente;
import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.model.MedicamentoAtencion;
import com.controlpacientes.service.PacienteService;
import com.controlpacientes.service.FichaMedicaService;
import com.controlpacientes.service.MedicamentoAtencionService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    @FXML private Button btnNewPaciente;
    @FXML private Button btnPacienteNuevo;
    @FXML private Button btnPacienteEditar;
    @FXML private Button btnPacienteEliminar;

    // Responsive Layout Controls
    @FXML private HBox mainContentHBox;
    @FXML private VBox leftColumn;
    @FXML private VBox rightColumn;
    @FXML private HBox pacienteColumnsContainer;
    @FXML private HBox searchColumnsContainer;

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
        setupResponsiveLayout();
        setupTabNavigation();
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

    private void setupTabNavigation() {
        // Configurar el orden de tabulación para los campos del paciente
        // Fila 1: RUT -> Nombre -> Apellido
        // Fila 2: Teléfono -> Email -> Dirección -> Ciudad
        // El orden natural debería funcionar ya que los campos están en orden en el FXML
        
        // Asegurar que los campos tengan focusTraversable habilitado
        tfPacienteRut.setFocusTraversable(true);
        tfPacienteNombre.setFocusTraversable(true);
        tfPacienteApellido.setFocusTraversable(true);
        tfPacienteTelefono.setFocusTraversable(true);
        tfPacienteEmail.setFocusTraversable(true);
        tfPacienteDireccion.setFocusTraversable(true);
        tfPacienteCiudad.setFocusTraversable(true);
    }

    private void cargarPacientes() {
        log.info("Cargando lista de pacientes...");
        List<Paciente> pacientes = pacienteService.findAll();
        log.info("Total pacientes en BD: {}", pacientes.size());
        
        // Ordenar alfabéticamente por nombre
        pacientes.sort(Comparator.comparing(p -> p.getNombre() != null ? p.getNombre() : ""));
        tvPacientes.getItems().setAll(pacientes);
        log.info("Tabla de pacientes actualizada con {} registros", pacientes.size());
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
        log.info("Limpiando formulario de ficha médica");
        taMotivoconsulta.clear();
        taSintesisDiagnostica.clear();
        limpiarMedicamentos();
    }

    @FXML
    private void handleFichaGuardar() {
        log.info("=== GUARDAR FICHA MÉDICA ===");
        try {
            // Validar que RUT y Nombre sean obligatorios
            String rut = tfPacienteRut.getText().trim();
            String nombre = tfPacienteNombre.getText().trim();
            
            log.info("Validando datos: RUT='{}', Nombre='{}'", rut, nombre);
            
            if (rut.isEmpty() || nombre.isEmpty()) {
                log.warn("VALIDACIÓN FALLIDA: RUT o Nombre vacíos");
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "RUT y Nombre del paciente son requeridos");
                return;
            }

            log.info("Validación exitosa");
            
            // Crear paciente si no existe
            if (pacienteSeleccionado == null) {
                log.info("Creando nuevo paciente");
                pacienteSeleccionado = new Paciente();
            } else {
                log.info("Usando paciente existente con ID: {}", pacienteSeleccionado.getId());
            }

            // Guardar información del paciente
            log.info("Estableciendo datos del paciente...");
            pacienteSeleccionado.setRut(rut);
            pacienteSeleccionado.setNombre(nombre);
            pacienteSeleccionado.setApellido(tfPacienteApellido.getText().trim());
            pacienteSeleccionado.setEmail(tfPacienteEmail.getText().trim());
            pacienteSeleccionado.setTelefono(tfPacienteTelefono.getText().trim());
            pacienteSeleccionado.setCiudad(tfPacienteCiudad.getText().trim());
            pacienteSeleccionado.setDireccion(tfPacienteDireccion.getText().trim());
            
            log.info("Guardando paciente en BD...");
            Paciente pacienteGuardado = pacienteService.save(pacienteSeleccionado);
            pacienteSeleccionado = pacienteGuardado;
            log.info("Paciente guardado con ID: {}", pacienteGuardado.getId());

            // Crear o editar ficha médica
            FichaMedica ficha;
            if (fichaSeleccionada != null && fichaSeleccionada.getId() != null) {
                // Editar ficha existente
                log.info("Editando ficha existente con ID: {}", fichaSeleccionada.getId());
                ficha = fichaSeleccionada;
            } else {
                // Crear nueva ficha
                log.info("Creando nueva ficha médica");
                ficha = new FichaMedica();
                ficha.setPaciente(pacienteGuardado);
                ficha.setFechaAtencion(LocalDateTime.now());
            }
            
            String motivo = taMotivoconsulta.getText().trim();
            String diagnostico = taSintesisDiagnostica.getText().trim();
            log.info("Motivo consulta: '{}', Diagnóstico: '{}'", motivo, diagnostico);
            
            ficha.setMotivoConsulta(motivo);
            ficha.setDiagnostico(diagnostico);

            log.info("Guardando ficha médica en BD...");
            FichaMedica fichaguardada = fichaMedicaService.save(ficha);
            fichaSeleccionada = fichaguardada;
            log.info("Ficha médica guardada con ID: {}", fichaguardada.getId());

            // Guardar medicamentos desde los TextFields
            // Primero eliminar medicamentos existentes si es edición
            if (fichaguardada.getId() != null && !medicamentosActuales.isEmpty()) {
                log.info("Eliminando medicamentos existentes: {}", medicamentosActuales.size());
                for (MedicamentoAtencion med : medicamentosActuales) {
                    if (med.getId() != null) {
                        medicamentoService.delete(med.getId());
                        log.info("Medicamento eliminado: {}", med.getId());
                    }
                }
                medicamentosActuales.clear();
            }

            // Guardar nuevos medicamentos
            log.info("Procesando medicamentos...");
            int medicamentosGuardados = 0;
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
                            medicamentosGuardados++;
                            log.info("Medicamento guardado: '{}'", nombreMedicamento);
                        }
                    }
                }
                indice++;
            }
            log.info("Total medicamentos guardados: {}", medicamentosGuardados);

            log.info("=== GUARDADO EXITOSO ===");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Paciente y ficha médica guardados correctamente");
            habilitarCamposPaciente(false);
            cargarFichasDelPaciente();
            cargarPacientes();
            log.info("Tabla de pacientes actualizada");
        } catch (Exception e) {
            log.error("ERROR AL GUARDAR", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
            e.printStackTrace();
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
        habilitarCamposFichaMedica(true);
        cargarFichasDelPaciente();
    }

    private void cargarFichasDelPaciente() {
        if (pacienteSeleccionado != null) {
            List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(pacienteSeleccionado.getId());
            
            // Formatear fechas a formato dd/MM/yyyy HH:mm para incluir la hora y diferenciar fichas del mismo día
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            List<String> fechasFormateadas = fichas.stream()
                    .map(FichaMedica::getFechaAtencion)
                    .sorted(Comparator.reverseOrder())
                    .map(fecha -> fecha.format(formatter))
                    .collect(Collectors.toList());
            
            // Agregar la fecha de hoy al principio para crear una nueva ficha
            String fechaHoy = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        java.time.LocalDateTime fechaBuscada = java.time.LocalDateTime.parse(fechaFormateada, formatter);
        
        List<FichaMedica> fichas = fichaMedicaService.findByPacienteId(paciente.getId())
                .stream()
                .filter(f -> {
                    // Comparar por fecha y hora exacta (hasta minutos)
                    java.time.LocalDateTime fechaFicha = f.getFechaAtencion();
                    return fechaFicha.getYear() == fechaBuscada.getYear() &&
                           fechaFicha.getMonthValue() == fechaBuscada.getMonthValue() &&
                           fechaFicha.getDayOfMonth() == fechaBuscada.getDayOfMonth() &&
                           fechaFicha.getHour() == fechaBuscada.getHour() &&
                           fechaFicha.getMinute() == fechaBuscada.getMinute();
                })
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
        habilitarCamposFichaMedica(false);
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
    private void handleNewPaciente() {
        log.info("=== NUEVO PACIENTE ===");
        // Crear un nuevo paciente vacío
        pacienteSeleccionado = new Paciente();
        log.info("Paciente nuevo creado: {}", pacienteSeleccionado);
        
        // Habilitar los campos del paciente para edición
        habilitarCamposPaciente(true);
        log.info("Campos de paciente habilitados");
        
        // Limpiar los campos del paciente
        tfPacienteRut.clear();
        tfPacienteNombre.clear();
        tfPacienteApellido.clear();
        tfPacienteEmail.clear();
        tfPacienteTelefono.clear();
        tfPacienteCiudad.clear();
        tfPacienteDireccion.clear();
        
        // Habilitar y limpiar la sección de fichas médicas
        habilitarCamposFichaMedica(true);
        log.info("Campos de ficha médica habilitados");
        
        // Agregar la fecha de hoy con "(Nueva)" al cbFichaFechas
        String fechaHoy = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        cbFichaFechas.getItems().clear();
        cbFichaFechas.getItems().add(fechaHoy + " (Nueva)");
        cbFichaFechas.getSelectionModel().select(0);
        log.info("Fecha hoy agregada al combo: {}", fechaHoy);
        
        taMotivoconsulta.clear();
        taSintesisDiagnostica.clear();
        limpiarMedicamentos();
        
        // Establecer foco en el primer campo
        tfPacienteRut.requestFocus();
        log.info("Foco establecido en RUT");
    }

    private void habilitarCamposFichaMedica(boolean habilitar) {
        cbFichaFechas.setDisable(!habilitar);
        taMotivoconsulta.setDisable(!habilitar);
        taSintesisDiagnostica.setDisable(!habilitar);
        btnFichaGuardar.setDisable(!habilitar);
        btnFichaLimpiar.setDisable(!habilitar);
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

    // ==================== RESPONSIVE LAYOUT METHODS ====================
    
    private void setupResponsiveLayout() {
        // Esperar a que la escena esté disponible
        if (mainContentHBox.getScene() != null) {
            attachWindowResizeListener();
        } else {
            mainContentHBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    attachWindowResizeListener();
                }
            });
        }
    }

    private void attachWindowResizeListener() {
        Stage stage = (Stage) mainContentHBox.getScene().getWindow();
        
        // Verificar que el stage no sea nulo
        if (stage == null) {
            return;
        }
        
        // Listener para cambios de ancho
        stage.widthProperty().addListener((obs, oldVal, newVal) -> 
            updateResponsiveLayout(newVal.doubleValue(), stage.getHeight())
        );
        
        // Listener para cambios de alto
        stage.heightProperty().addListener((obs, oldVal, newVal) -> 
            updateResponsiveLayout(stage.getWidth(), newVal.doubleValue())
        );
        
        // Aplicar layout inicial
        updateResponsiveLayout(stage.getWidth(), stage.getHeight());
    }

    private void updateResponsiveLayout(double width, double height) {
        boolean isSmallScreen = width < 1920 || height < 1200;
        
        if (isSmallScreen) {
            // Para pantallas pequeñas: cambiar a layout apilado
            leftColumn.setPrefWidth(Double.MAX_VALUE);
            rightColumn.setPrefWidth(Double.MAX_VALUE);
        } else {
            // Para pantallas grandes: mantener layout horizontal
            leftColumn.setPrefWidth(750.0);
            rightColumn.setPrefWidth(400.0);
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
