package com.controlpacientes.service;

import com.controlpacientes.model.FichaMedica;
import com.controlpacientes.model.MedicamentoAtencion;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PDFGeneratorService {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un PDF con los datos de la ficha médica
     *
     * @param ficha           La ficha médica a generar
     * @param outputPath      Ruta del archivo PDF a crear
     * @throws IOException    Si ocurre un error en la generación del PDF
     */
    public void generateFichaMedicaPDF(FichaMedica ficha, String outputPath) throws IOException {
        // Crear el escritor de PDF
        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título principal
        Paragraph title = new Paragraph("FICHA MÉDICA")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Información del paciente
        document.add(createSectionTitle("INFORMACIÓN DEL PACIENTE"));

        Table pacienteTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));
        pacienteTable.setMarginBottom(15);

        addTableRow(pacienteTable, "RUT:", ficha.getPaciente().getRut());
        addTableRow(pacienteTable, "Nombre:", ficha.getPaciente().getNombre() + " " + ficha.getPaciente().getApellido());
        addTableRow(pacienteTable, "Email:", ficha.getPaciente().getEmail());
        addTableRow(pacienteTable, "Teléfono:", ficha.getPaciente().getTelefono());
        addTableRow(pacienteTable, "Ciudad:", ficha.getPaciente().getCiudad());
        addTableRow(pacienteTable, "Dirección:", ficha.getPaciente().getDireccion());

        document.add(pacienteTable);

        // Información de la ficha médica
        document.add(createSectionTitle("INFORMACIÓN DE LA ATENCIÓN"));

        Table fichaTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));
        fichaTable.setMarginBottom(15);

        addTableRow(fichaTable, "Fecha de Atención:", formatDate(ficha.getFechaAtencion()));
        addTableRow(fichaTable, "Profesional:", ficha.getProfesionalNombre());

        document.add(fichaTable);

        // Motivo de consulta
        document.add(createSectionTitle("MOTIVO DE LA CONSULTA"));
        document.add(new Paragraph(ficha.getMotivoConsulta() != null ? ficha.getMotivoConsulta() : "N/A")
                .setMarginBottom(15));

        // Diagnóstico
        document.add(createSectionTitle("DIAGNÓSTICO"));
        document.add(new Paragraph(ficha.getDiagnostico() != null ? ficha.getDiagnostico() : "N/A")
                .setMarginBottom(15));

        // Medicamentos
        if (ficha.getMedicamentos() != null && !ficha.getMedicamentos().isEmpty()) {
            document.add(createSectionTitle("MEDICAMENTOS"));

            Table medicamentosTable = new Table(1)
                    .setWidth(UnitValue.createPercentValue(100));
            medicamentosTable.setMarginBottom(15);

            for (MedicamentoAtencion medicamento : ficha.getMedicamentos()) {
                Cell cell = new Cell()
                        .add(new Paragraph("• " + 
                                (medicamento.getNombreMedicamento() != null ? 
                                        medicamento.getNombreMedicamento() : "N/A")))
                        .setPadding(5);
                medicamentosTable.addCell(cell);
            }

            document.add(medicamentosTable);
        }

        // Pie de página con fecha de generación
        document.add(new Paragraph()
                .setMarginTop(30));

        document.add(new Paragraph("Documento generado el " + 
                LocalDateTime.now().format(DATETIME_FORMATTER))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));

        document.close();
    }

    /**
     * Genera la ruta de archivo por defecto para una ficha médica
     *
     * @param ficha La ficha médica
     * @return Ruta del archivo sugerida
     */
    public String generateDefaultFilePath(FichaMedica ficha) {
        String fileName = String.format("Ficha_%s_%s.pdf",
                ficha.getPaciente().getRut().replace("-", ""),
                System.currentTimeMillis());
        
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
        return downloadsPath + File.separator + fileName;
    }

    // Métodos auxiliares privados

    private Paragraph createSectionTitle(String title) {
        return new Paragraph(title)
                .setFontSize(12)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
    }

    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold())
                .setPadding(5);
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value != null ? value : "N/A"))
                .setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
}
