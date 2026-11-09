import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TransactionService } from '../../../../core/services/transaction.service';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { AgenceZoneService } from '../../../../core/services/gestion-agence-zone.service';
import { PdfReceiptService } from '../../../../core/services/pdf-receipt.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-recu',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recu.html',
  styleUrls: ['./recu.css']
})
export class RecuComponent implements OnInit {
  transactionId: string | null = null;
  message: string | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private txService: TransactionService,
    private auth: AuthService,
    private agenceService: AgenceZoneService,
    private pdfReceiptService: PdfReceiptService
  ) {}

  ngOnInit(): void {
    this.transactionId = this.route.snapshot.queryParamMap.get('id') || null;
    if (this.transactionId) {
      // Pre-fill and optionally auto-generate? we only display control here
    }
  }

  generate(): void {
    if (!this.transactionId) { this.message = 'Veuillez fournir un ID de transaction.'; return; }
    this.loading = true;
    this.message = null;
    this.txService.generateReceipt(this.transactionId).subscribe({
      next: async (res) => {
        // Traiter réponse texte: URL, chemin, base64 ou message
        try {
          if (typeof res === 'string') {
            const text = res.trim();
            if (text.startsWith('http') || text.startsWith('/') || text.toLowerCase().includes('.pdf')) {
              const url = text.startsWith('http') ? text : `${environment.apiUrl}${text}`;
              window.open(url, '_blank');
              this.loading = false;
              return;
            }
            if (text.startsWith('%PDF') || text.startsWith('JVBER')) {
              const byteCharacters = atob(text);
              const byteNumbers = new Array(byteCharacters.length);
              for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
              }
              const byteArray = new Uint8Array(byteNumbers);
              const blob = new Blob([byteArray], { type: 'application/pdf' });
              const blobUrl = URL.createObjectURL(blob);
              window.open(blobUrl, '_blank');
              this.loading = false;
              return;
            }
            // Si message placeholder, simuler un reçu HTML pour test local
            if (text.includes('À implémenter') || text.toLowerCase().includes('génération')) {
              try {
                  // Récupérer les données de transaction côté client (si disponibles)
                  let tx: any = null;
                  try {
                    const txs = await firstValueFrom(this.txService.getAll());
                    tx = (txs || []).find((t: any) => String(t.idTransaction) === String(this.transactionId));
                  } catch (_) {
                    tx = null;
                  }

                  const logoResp = await fetch('/img/Savely.png');
                  const logoBlob = await logoResp.blob();
                  const reader = new FileReader();
                  const dataUrl: string = await new Promise((resolve, reject) => {
                    reader.onload = () => resolve(reader.result as string);
                    reader.onerror = (e) => reject(e);
                    reader.readAsDataURL(logoBlob);
                  });

                  const { jsPDF } = await import('jspdf');
                  const doc = new jsPDF({ unit: 'pt', format: 'A4' });
                  const pageWidth = doc.internal.pageSize.getWidth();
                  const pageHeight = doc.internal.pageSize.getHeight();
                  const margin = 40;

                  // Resolve agency code
                  const user = this.auth?.getUser ? this.auth.getUser() : null;
                  let agenceCode = 'N/A';
                  try {
                    const idAgence = user?.idAgence;
                    if (idAgence) {
                      const agences = await firstValueFrom(this.agenceService.getAll());
                      const found = (agences || []).find((a: any) => String(a.idAgence) === String(idAgence));
                      agenceCode = found?.code || String(idAgence);
                    }
                  } catch (e) {
                    agenceCode = user?.idAgence ? String(user.idAgence) : 'N/A';
                  }

                  // Draw styled header
                  const reference = tx?.reference || '';
                  let y = this.pdfReceiptService.drawStyledHeader(doc, dataUrl, pageWidth, margin, agenceCode, reference);

                  // Prepare amount data
                  const txId = tx?.idTransaction || this.transactionId;
                  const montantNum = tx?.montant ? Number(tx.montant) : 0;
                  const montant = new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'XAF' }).format(montantNum || 0);
                  const montantStr = String(montant).replace(/\u00A0/g, ' ').replace(/\s+/g, ' ').trim();
                  const montantEnLettres = this.numberToWordsFR(Math.round(montantNum || 0));

                  // Info block
                  const leftX = margin;
                  const labelX = leftX + 10;
                  const valueX = leftX + 150;

                  // Initiator / collector
                  doc.setFontSize(11);
                  doc.setFont('helvetica', 'normal');
                  doc.setTextColor(0, 0, 0);
                  const initiateur = tx?.nomInitiateur || tx?.clientName || tx?.nomClient || tx?.idEmployeInitiateur || 'Client inconnu';
                  const caissier = tx?.nomCaissier || tx?.idCaissierValidateur || 'N/A';
                  const dateTx = tx?.dateTransaction ? new Date(tx.dateTransaction).toLocaleString() : new Date().toLocaleString();

                  doc.text('Reçu de :', labelX, y);
                  doc.text(String(initiateur), valueX, y);
                  y += 20;

                  doc.text('Référence :', labelX, y);
                  doc.text(String(reference), valueX, y);
                  y += 20;

                  doc.text('Date :', labelX, y);
                  doc.text(String(dateTx), valueX, y);
                  y += 20;

                  // Draw amount box (styled)
                  const boxResult = this.pdfReceiptService.drawAmountBox(doc, montantStr, pageWidth, margin, y);
                  y = boxResult.boxY + boxResult.boxH + 12;

                  // Amount in words (wrapped, auto-shrink)
                  y = this.pdfReceiptService.drawAmountInWords(doc, montantEnLettres, leftX, 90, pageWidth, margin, y);

                  y += 12;
                  doc.text('Caissier :', labelX, y);
                  doc.text(String(caissier), valueX, y);
                  y += 20;
                  y += 12;
                  doc.setFont('helvetica', 'bold');
                  doc.text('Détails', labelX, y);
                  doc.setFont('helvetica', 'normal');
                  y += 18;
                  const detailsLines = doc.splitTextToSize(text, pageWidth - margin * 2 - 20);
                  doc.text(detailsLines, labelX, y);

                  doc.setFontSize(10);
                  doc.setTextColor(120, 120, 120);
                  doc.text('Savely - Reçu généré (simulation)', pageWidth - margin, pageHeight - 40, { align: 'right' });

                  const fileName = `recu_${txId}.pdf`;
                  doc.save(fileName);
                  this.loading = false;
                  return;
                } catch (e) {
                console.warn('Génération PDF client échouée, ouverture de la simulation HTML', e);
                const html = `
                <html><head><title>Reçu (Simulation)</title></head><body>
                <h1>Reçu (Simulation)</h1>
                <p>ID Transaction: ${this.transactionId}</p>
                <p>Message serveur: ${text}</p>
                <hr/>
                <p>Ce reçu est une simulation locale. Implémentez la génération PDF côté backend pour un vrai reçu.</p>
                </body></html>`;
                const blob = new Blob([html], { type: 'text/html' });
                const url = URL.createObjectURL(blob);
                window.open(url, '_blank');
                this.loading = false;
                return;
              }
            }
            this.message = 'Reçu généré (serveur): ' + text;
            this.loading = false;
            return;
          }
        } catch (e) {
          console.warn('Traitement réponse reçu failed', e);
        }
        this.message = 'Reçu généré avec succès.';
        this.loading = false;
      },
      error: (err) => {
        // Log détaillé pour debugging
        try {
          console.error('Erreur génération reçu', {
            idTransaction: this.transactionId,
            status: err.status,
            message: err.message,
            errorBody: err.error,
            headers: err.headers
          });
        } catch (e) {
          console.error('Erreur génération reçu (log failed)', err);
        }
        this.message = 'Erreur lors de la génération du reçu (voir console pour détails).';
        this.loading = false;
      }
    });
  }

  // Helpers for amount-in-words and capitalization
  private capitalizeFirst(s: string): string { if (!s) return ''; return s.charAt(0).toUpperCase() + s.slice(1); }

  private numberToWordsFR(n: number): string {
    if (n === 0) return 'zéro';
    const units: any = ['','un','deux','trois','quatre','cinq','six','sept','huit','neuf','dix','onze','douze','treize','quatorze','quinze','seize'];
    const tens: any = ['','','vingt','trente','quarante','cinquante','soixante'];

    const underHundred = (num: number): string => {
      if (num < 17) return units[num];
      if (num < 20) return 'dix-' + units[num - 10];
      if (num < 70) {
        const t = Math.floor(num / 10);
        const u = num % 10;
        const base = tens[t];
        if (u === 1) return base + ' et un';
        return base + (u ? '-' + units[u] : '');
      }
      if (num < 80) return 'soixante' + (num === 71 ? '-et-onze' : '-' + underHundred(num - 60));
      if (num < 100) return 'quatre-vingt' + (num === 80 ? 's' : (num === 81 ? '-un' : '-' + underHundred(num - 80)));
      return '';
    };

    const parts: string[] = [];
    const billions = Math.floor(n / 1_000_000_000);
    if (billions) { parts.push((billions > 1 ? this.numberToWordsFR(billions) + ' ' : '') + 'milliard' + (billions > 1 ? 's' : '')); n %= 1_000_000_000; }
    const millions = Math.floor(n / 1_000_000);
    if (millions) { parts.push((millions > 1 ? this.numberToWordsFR(millions) + ' ' : '') + 'million' + (millions > 1 ? 's' : '')); n %= 1_000_000; }
    const thousands = Math.floor(n / 1000);
    if (thousands) { parts.push((thousands > 1 ? this.numberToWordsFR(thousands) + ' ' : '') + 'mille'); n %= 1000; }

    const hundreds = Math.floor(n / 100);
    if (hundreds) {
      if (hundreds === 1) parts.push('cent');
      else parts.push(units[hundreds] + ' cent' + (n % 100 === 0 ? 's' : ''));
      n %= 100;
    }
    if (n) parts.push(underHundred(n));
    return parts.join(' ').replace(/\s+/g, ' ').trim();
  }
}
