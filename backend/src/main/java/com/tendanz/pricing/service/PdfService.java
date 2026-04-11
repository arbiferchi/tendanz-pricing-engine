package com.tendanz.pricing.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.exception.PdfGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for generating PDF documents for Quotes (Devis).
 * Applies SOLID principles (Single Responsibility Principle) by separating PDF logic from business pricing logic.
 */
@Slf4j
@Service
public class PdfService {

    /**
     * Generates a PDF representing the provided QuoteResponse.
     *
     * @param quote the quote response containing the data to export
     * @return a byte array containing the PDF data
     */
    public byte[] generateQuotePdf(QuoteResponse quote) {
        log.info("Generating PDF for Quote ID: {}", quote.getQuoteId());
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("Devis d'Assurance / Insurance Quote", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Quote Information
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(20);

            addTableRow(infoTable, "Numéro de Devis :", String.valueOf(quote.getQuoteId()), normalFont, tableHeaderFont);
            addTableRow(infoTable, "Client:", quote.getClientName() + " (" + quote.getClientAge() + " ans)", normalFont, tableHeaderFont);
            addTableRow(infoTable, "Produit (Product):", quote.getProductName(), normalFont, tableHeaderFont);
            addTableRow(infoTable, "Zone:", quote.getZoneName(), normalFont, tableHeaderFont);
            
            // Format Calculation Date
            String dateFormatted = quote.getCreatedAt() != null 
                    ? quote.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) 
                    : "N/A";
            addTableRow(infoTable, "Date du Calcul:", dateFormatted, normalFont, tableHeaderFont);

            document.add(infoTable);

            // Price Section
            Paragraph priceTitle = new Paragraph("Résumé Financier ", sectionTitleFont);
            priceTitle.setSpacingAfter(10);
            document.add(priceTitle);

            PdfPTable priceTable = new PdfPTable(2);
            priceTable.setWidthPercentage(100);
            priceTable.setSpacingAfter(20);

            addTableRow(priceTable, "Tarif de Base :", quote.getBasePrice() + " TND", normalFont, tableHeaderFont);
            
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
            addTableRow(priceTable, "Prix Final :", quote.getFinalPrice() + " TND", totalFont, tableHeaderFont);
            
            document.add(priceTable);

            // Applied Rules Section
            Paragraph rulesTitle = new Paragraph("Règles Appliquées ", sectionTitleFont);
            rulesTitle.setSpacingAfter(10);
            document.add(rulesTitle);

            if (quote.getAppliedRules() != null && !quote.getAppliedRules().isEmpty()) {
                com.lowagie.text.List rulesList = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                for (String rule : quote.getAppliedRules()) {
                    rulesList.add(new com.lowagie.text.ListItem(rule, normalFont));
                }
                document.add(rulesList);
            } else {
                document.add(new Paragraph("Aucune règle spécifique appliquée.", normalFont));
            }

            // Footer
            Paragraph footer = new Paragraph("Généré automatiquement par Tendanz Pricing Engine.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY));
            footer.setSpacingBefore(30);
            document.add(footer);

        } catch (DocumentException e) {
            log.error("Error generating PDF document for Quote ID: {}", quote.getQuoteId(), e);
            throw new PdfGenerationException("Erreur lors de la génération du PDF pour le devis " + quote.getQuoteId(), e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private void addTableRow(PdfPTable table, String label, String value, Font valueFont, Font headerFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, headerFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPaddingBottom(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPaddingBottom(5);
        table.addCell(valueCell);
    }
}
