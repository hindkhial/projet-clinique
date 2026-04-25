package com.ehei.rapport.service;

import com.ehei.rapport.model.Rapport;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfGeneratorService {

    public InputStream generatePdf(Rapport rapport) {
        log.info("Generating PDF for rapport ID: {}", rapport.getId());

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // === HEADER ===
            document.add(new Paragraph("RAPPORT MÉDICAL")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            document.add(new Paragraph("\n"));

            // === RAPPORT INFO ===
            addLine(document, "Rapport N°: " + rapport.getId());
            addLine(document, "Date: " + formatDate(rapport.getDateRapport()));
            addLine(document, "Type: " + rapport.getTypeRapport());
            document.add(new Paragraph("\n"));

            // === PATIENT INFO ===
            addSection(document, "INFORMATIONS PATIENT");
            addLine(document, "Nom: " + getValue(rapport.getPatientNom()));
            addLine(document, "Prénom: " + getValue(rapport.getPatientPrenom()));
            document.add(new Paragraph("\n"));

            // === MEDECIN INFO ===
            addSection(document, "MÉDECIN TRAITANT");
            addLine(document, "Dr. " + getValue(rapport.getMedecinNom()) + " " +
                    getValue(rapport.getMedecinPrenom()));
            addLine(document, "Spécialité: " + getValue(rapport.getMedecinSpecialite()));
            document.add(new Paragraph("\n"));

            // === DIAGNOSTIC ===
            if (rapport.getDiagnostic() != null || rapport.getTypeMaladie() != null) {
                addSection(document, "DIAGNOSTIC");
                if (rapport.getTypeMaladie() != null) {
                    addLine(document, "Type de Maladie: " + rapport.getTypeMaladie());
                }
                if (rapport.getDiagnostic() != null) {
                    addLine(document, rapport.getDiagnostic());
                }
                document.add(new Paragraph("\n"));
            }

            // === OBSERVATIONS ===
            if (rapport.getObservations() != null) {
                addSection(document, "OBSERVATIONS");
                addLine(document, rapport.getObservations());
                document.add(new Paragraph("\n"));
            }

            // === TRAITEMENT ===
            if (rapport.getTraitement() != null) {
                addSection(document, "TRAITEMENT PRESCRIT");
                addLine(document, rapport.getTraitement());
                document.add(new Paragraph("\n"));
            }

            // === SIGNATURE ===
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("_______________________")
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Signature du Médecin")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10));

            // Close document
            document.close();

            log.info("PDF generated successfully");

            // Convert to InputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    // Helper: Add section title
    private void addSection(Document doc, String title) {
        doc.add(new Paragraph(title).setFontSize(14).setBold());
    }

    // Helper: Add regular line
    private void addLine(Document doc, String text) {
        doc.add(new Paragraph(text).setFontSize(11));
    }

    // Helper: Get value or "N/A"
    private String getValue(String value) {
        return value != null ? value : "N/A";
    }

    // Helper: Format date
    private String formatDate(java.time.LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
    }
}

