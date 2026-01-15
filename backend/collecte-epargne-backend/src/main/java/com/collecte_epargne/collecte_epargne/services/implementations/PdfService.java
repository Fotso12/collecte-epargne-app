package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    public ByteArrayInputStream generateTransactionReceipt(Transaction transaction) {
        Document document = new Document(PageSize.A5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Police
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Titre
            Paragraph title = new Paragraph("SAVELY - RECU DE TRANSACTION", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table des informations
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            addCell(table, "Référence:", fontLabel);
            addCell(table, transaction.getReference(), fontValue);

            addCell(table, "Date:", fontLabel);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            addCell(table, formatter.format(transaction.getDateTransaction()), fontValue);

            addCell(table, "Client:", fontLabel);
            String clientName = transaction.getCompte().getClient().getUtilisateur().getNom() + " " + transaction.getCompte().getClient().getUtilisateur().getPrenom();
            addCell(table, clientName, fontValue);

            addCell(table, "Type:", fontLabel);
            addCell(table, transaction.getTypeTransaction().name(), fontValue);

            addCell(table, "Montant:", fontLabel);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "CM"));
            String formattedAmount = currencyFormat.format(transaction.getMontant());
            addCell(table, formattedAmount, fontValue);

            addCell(table, "Statut:", fontLabel);
            addCell(table, transaction.getStatut().name(), fontValue);

            document.add(table);

            // Pied de page
            Paragraph footer = new Paragraph("\n\nMerci de votre confiance.", fontValue);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
