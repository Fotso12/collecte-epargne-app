import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';

/**
 * Service utilitaire pour générer des reçus PDF uniformes
 * Centralise la logique de style et mise en page pour éviter duplication
 */
@Injectable({ providedIn: 'root' })
export class PdfReceiptService {

  /**
   * Configure le style de la boîte montant (couleur, bordure, police) et affiche le montant
   */
  drawAmountBox(
    doc: jsPDF,
    montantDisplay: string,
    pageWidth: number,
    margin: number,
    y: number
  ): { boxY: number; boxH: number } {
    const amountBoxW = 240;
    const amountBoxH = 72;
    const amountBoxX = Math.max(margin, pageWidth - margin - amountBoxW);
    const amountBoxY = y;

    // Light background with subtle border
    doc.setFillColor(250, 251, 253);
    doc.rect(amountBoxX, amountBoxY, amountBoxW, amountBoxH, 'F');
    doc.setDrawColor(210, 215, 220);
    doc.setLineWidth(0.8);
    doc.rect(amountBoxX, amountBoxY, amountBoxW, amountBoxH);

    // Numeric amount: center and fit the box
    doc.setFont('helvetica', 'bold');
    let amountFontSize = 22;
    doc.setFontSize(amountFontSize);
    while (amountFontSize > 8 && doc.getTextWidth(montantDisplay) > amountBoxW - 32) {
      amountFontSize -= 1;
      doc.setFontSize(amountFontSize);
    }
    // Amount color (dark green)
    doc.setTextColor(11, 105, 11);
    doc.text(montantDisplay, amountBoxX + amountBoxW / 2, amountBoxY + amountBoxH / 2 + 8, { align: 'center' });

    // Currency label small under amount
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(80, 80, 80);
    doc.text('FCFA', amountBoxX + amountBoxW / 2, amountBoxY + amountBoxH - 10, { align: 'center' });

    doc.setTextColor(0, 0, 0);
    return { boxY: amountBoxY, boxH: amountBoxH };
  }

  /**
   * Draw styled header with logo, title, and agency pill
   */
  drawStyledHeader(
    doc: jsPDF,
    logoDataUrl: string | null,
    pageWidth: number,
    margin: number,
    agenceCode: string,
    reference: string
  ): number {
    const pageHeight = doc.internal.pageSize.getHeight();

    // Header background
    doc.setFillColor(10, 90, 160);
    doc.rect(0, 0, pageWidth, 110, 'F');

    // Logo
    const logoW = 120;
    const logoH = 40;
    if (logoDataUrl) {
      try { doc.addImage(logoDataUrl, 'PNG', margin, 20, logoW, logoH); } catch (e) {}
    }

    // Title
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(26);
    doc.setFont('helvetica', 'bold');
    doc.text('Reçu de transaction', pageWidth / 2, 64, { align: 'center' });

    // Separator
    doc.setDrawColor(200);
    doc.setLineWidth(0.8);
    doc.line(margin, 110, pageWidth - margin, 110);

    // Agency code pill (top right)
    doc.setFontSize(11);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(255, 255, 255);
    const agencyLabel = `Agence: ${agenceCode || 'N/A'}`;
    const agencyW = doc.getTextWidth(agencyLabel) + 14;
    const agencyX = pageWidth - margin - agencyW;
    const agencyY = 18;
    doc.setFillColor(8, 62, 110);
    doc.rect(agencyX, agencyY, agencyW, 18, 'F');
    doc.setTextColor(255, 255, 255);
    doc.text(agencyLabel, agencyX + agencyW - 8, agencyY + 13, { align: 'right' });

    // Reference (if present)
    if (reference) {
      doc.setFontSize(10);
      doc.setFont('helvetica', 'normal');
      doc.text(`Réf: ${reference}`, pageWidth - margin, agencyY + 36, { align: 'right' });
    }

    doc.setTextColor(0, 0, 0);
    return 130; // Return y position for content after header
  }

  /**
   * Draw footer with signature line and agency code
   */
  drawFooter(
    doc: jsPDF,
    leftX: number,
    labelWidth: number,
    pageWidth: number,
    pageHeight: number,
    margin: number,
    agenceCode: string,
    signatureY: number
  ): void {
    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    doc.text('Signature :', leftX, signatureY);
    doc.line(leftX + labelWidth, signatureY + 6, leftX + labelWidth + 300, signatureY + 6);

    doc.setFontSize(9);
    doc.setTextColor(120, 120, 120);
    doc.text(`Code agence: ${agenceCode}`, leftX, pageHeight - margin - 24);
    doc.text('Savely - Reçu généré (simulation)', pageWidth - margin, pageHeight - margin - 24, { align: 'right' });
    doc.setTextColor(0, 0, 0);
  }

  /**
   * Wrap and display amount-in-words with automatic font size reduction if needed
   */
  drawAmountInWords(
    doc: jsPDF,
    montantTxt: string,
    leftX: number,
    labelWidth: number,
    pageWidth: number,
    margin: number,
    y: number
  ): number {
    doc.setFont('helvetica', 'normal');
    let mots = montantTxt.charAt(0).toUpperCase() + montantTxt.slice(1);
    let motFont = 10;
    doc.setFontSize(motFont);
    const fieldWidth = pageWidth - margin * 2 - labelWidth - 60;
    let wrapped = doc.splitTextToSize(mots, fieldWidth);
    while (wrapped.length > 3 && motFont > 7) {
      motFont -= 1;
      doc.setFontSize(motFont);
      wrapped = doc.splitTextToSize(mots, fieldWidth);
    }
    doc.text(wrapped, leftX + labelWidth, y);
    return y + wrapped.length * (motFont + 2);
  }
}
