import { Component, OnInit, ChangeDetectorRef, OnDestroy, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, Subscription, firstValueFrom } from 'rxjs';
import { StatutTransaction } from '../../../../donnees/modeles/enums';
import { TransactionService } from '../../../../core/services/transaction.service';
import { EmployeService } from '../../../../core/services/employe.service';
import { AuthService } from '../../../../core/services/auth.service';
import { AgenceZoneService } from '../../../../core/services/gestion-agence-zone.service';
import { PdfReceiptService } from '../../../../core/services/pdf-receipt.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-liste-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './liste-transactions.html',
  styleUrls: ['./liste-transactions.css']
})
export class ListeTransactionsComponent implements OnInit, OnDestroy {
  // --- Propriétés originales préservées ---
  transactions: any[] = [];
  transactionsFiltrees: any[] = [];
  employes: any[] = [];
  user: any = null;
  private authSub?: Subscription;

  stats = { total: 0, validees: 0, pourcentage: 0 };
  filtreReference = '';
  filtreStatut = '';
  filtreType = '';

  chargement = false;
  isDetailsModalOpen = false;
  transactionSelectionnee?: any;
  StatutTx = StatutTransaction;

  // --- Nouveaux états pour les modals améliorés ---
  isConfirmModalOpen = false;
  isRejectModalOpen = false;
  motifRejetTexte = '';

  constructor(
    private transactionService: TransactionService,
    private employeService: EmployeService,
    private authService: AuthService,
    private agenceService: AgenceZoneService,
    private pdfReceiptService: PdfReceiptService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) { }

  ngOnInit(): void {
    this.recupererUtilisateur();
    if (this.authService?.currentUser$) {
      this.authSub = this.authService.currentUser$.subscribe(userData => {
        if (userData) {
          this.zone.run(() => {
            this.user = userData;
            if (this.transactions.length === 0) this.chargerDonnees();
          });
        }
      });
    }
    this.chargerDonnees();
  }

  ngOnDestroy(): void {
    if (this.authSub) this.authSub.unsubscribe();
  }

  private recupererUtilisateur() {
    try {
      this.user = this.authService.getUser() || JSON.parse(localStorage.getItem('auth_user') || 'null');
    } catch (e) {
      console.warn("Session utilisateur non trouvée au démarrage.");
    }
  }

  chargerDonnees = () => {
    if (this.chargement) return;
    this.chargement = true;
    this.forcerRendu();

    forkJoin({
      txs: this.transactionService.getAll(),
      emps: this.employeService.getAll()
    }).subscribe({
      next: (resultat) => {
        this.zone.run(() => {
          this.employes = resultat.emps || [];
          this.transactions = (resultat.txs || []).map(t => ({
            ...t,
            nomInitiateur: this.trouverNomComplet(t.idEmployeInitiateur),
            nomCaissier: this.trouverNomComplet(t.idCaissierValidateur)
          }));

          this.transactionsFiltrees = [...this.transactions];
          this.calculerStats();
          this.chargement = false;
          this.forcerRendu();
        });
      },
      error: (err) => {
        this.zone.run(() => {
          console.error('Erreur de chargement:', err);
          this.chargement = false;
          this.forcerRendu();
        });
      }
    });
  }

  private forcerRendu() {
    this.cdr.markForCheck();
    this.cdr.detectChanges();
    setTimeout(() => {
      this.cdr.detectChanges();
    }, 0);
  }

  private trouverNomComplet(idRecu?: any): string {
    if (!idRecu || !this.employes || this.employes.length === 0) return 'Non assigné';
    const idStr = String(idRecu).trim().toLowerCase();
    const emp = this.employes.find(e =>
      String(e.idEmploye) === idStr ||
      e.matricule?.toString().toLowerCase() === idStr ||
      e.loginUtilisateur?.toString().toLowerCase() === idStr
    );
    return emp ? `${emp.prenom} ${emp.nom}` : idRecu;
  }

  // --- Logique de validation avec Modal ---
  validerTransaction(id: string) {
    this.transactionSelectionnee = this.transactions.find(t => t.idTransaction === id);
    this.isConfirmModalOpen = true;
  }
    rejeterTransaction(id: string) {
      this.transactionSelectionnee = this.transactions.find(t => t.idTransaction === id);
      this.motifRejetTexte = '';
      this.isRejectModalOpen = true;
    }

    confirmerRejet() {
      if (!this.transactionSelectionnee) {
        alert('Aucune transaction sélectionnée.');
        return;
      }
      if (!this.motifRejetTexte || !this.motifRejetTexte.trim()) {
        alert('Veuillez indiquer le motif du rejet.');
        return;
      }

      const id = this.transactionSelectionnee.idTransaction;
      try {
        this.transactionService.rejeter(id, this.motifRejetTexte.trim()).subscribe({
          next: () => {
            // update local model for immediate feedback
            this.transactionSelectionnee.statut = 'REJETEE';
            this.transactionSelectionnee.motifRejet = this.motifRejetTexte.trim();
            this.isRejectModalOpen = false;
            this.motifRejetTexte = '';
            this.chargerDonnees();
            alert('Transaction rejetée.');
          },
          error: (err) => {
            console.error('Erreur lors du rejet', err);
            alert('Erreur lors du rejet (voir console pour détails).');
          }
        });
      } catch (e) {
        console.error('Erreur appel rejeter', e);
        alert('Erreur lors du rejet.');
      }
    }
  confirmerValidation() {
    const loginValide = this.user?.login || this.user?.email || JSON.parse(localStorage.getItem('auth_user') || '{}')?.login;
    if (!loginValide || !this.transactionSelectionnee) {
      alert('Erreur : Identifiant introuvable.');
      return;
    }

    const id = this.transactionSelectionnee.idTransaction;
    // If the transactionService exposes a server-side validation, use it; otherwise simulate.
    if ((this.transactionService as any).validerTransaction) {
      try {
        (this.transactionService as any).validerTransaction(id).subscribe({
          next: () => { alert('Transaction validée'); this.isConfirmModalOpen = false; this.chargerDonnees(); },
          error: (e: any) => { console.error('Erreur validation:', e); alert('Erreur lors de la validation'); }
        });
        return;
      } catch (e) {
        console.warn('Appel validerTransaction a échoué', e);
      }
    }

    // Fallback: update local model and refresh
    this.transactionSelectionnee.statut = 'VALIDEE';
    this.isConfirmModalOpen = false;
    this.chargerDonnees();
  }

  // --- Fonctions utilitaires préservées ---
  calculerStats() {
    this.stats.total = this.transactions.length;
    this.stats.validees = this.transactions.filter(t =>
      t.statut === 'TERMINEE' || t.statut === 'VALIDEE_SUPERVISEUR'
    ).length;
    this.stats.pourcentage = this.stats.total > 0 ? (this.stats.validees / this.stats.total) * 100 : 0;
  }

  appliquerFiltres() {
    const search = this.filtreReference.toLowerCase();
    this.transactionsFiltrees = this.transactions.filter(t =>
      (t.reference?.toLowerCase().includes(search) || t.nomInitiateur?.toLowerCase().includes(search)) &&
      (!this.filtreStatut || t.statut === this.filtreStatut) &&
      (!this.filtreType || t.typeTransaction === this.filtreType)
    );
  }

  chargerTransactions() { this.chargerDonnees(); }
  voirDetails(t: any) { this.transactionSelectionnee = t; this.isDetailsModalOpen = true; }

  fermerModal() {
    this.isDetailsModalOpen = false;
    this.isConfirmModalOpen = false;
    this.isRejectModalOpen = false;
  }

  // Génération de reçu (similaire à RecuComponent) pour le bouton dans la liste
  generateReceipt(id: string) {
    if (!id) return;
    try {
      this.transactionService.generateReceipt(id).subscribe({
        next: async (res: any) => {
          try {
            if (typeof res === 'string') {
              const text = res.trim();
              if (text.startsWith('http') || text.startsWith('/') || text.toLowerCase().includes('.pdf')) {
                const url = text.startsWith('http') ? text : `${environment.apiUrl}${text}`;
                window.open(url, '_blank');
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
                return;
              }

              // Placeholder -> generate client PDF
              if (text.includes('À implémenter') || text.toLowerCase().includes('génération')) {
                try {
                  let tx = this.transactions?.find(t => String(t.idTransaction) === String(id));
                  if (!tx) {
                    try {
                      const txs = await firstValueFrom(this.transactionService.getAll());
                      tx = (txs || []).find((t: any) => String(t.idTransaction) === String(id));
                    } catch (e) {
                      tx = null;
                    }
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
                  const user = this.authService?.getUser ? this.authService.getUser() : null;
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
                  const montantNum = tx?.montant ? Number(tx.montant) : 0;
                  const montantFmt = new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'XAF' }).format(montantNum || 0);
                  const montantDisplay = String(montantFmt).replace(/\u00A0/g, ' ').replace(/\s+/g, ' ').trim();
                  const montantTxt = this.numberToWordsFR(Math.round(montantNum || 0));

                  const leftX = margin;
                  const labelWidth = 90;

                  // Initiator / collector info
                  doc.setFontSize(11);
                  doc.setFont('helvetica', 'normal');
                  const initiateur = tx?.nomInitiateur || this.trouverNomComplet(tx?.idEmployeInitiateur) || 'Client';
                  doc.text('Reçu de :', leftX, y);
                  doc.text(initiateur, leftX + labelWidth, y);
                  y += 20;

                  // Collector label (italic)
                  doc.setFontSize(9);
                  doc.setFont('helvetica', 'italic');
                  doc.text(`Collecteur: ${initiateur}`, leftX + labelWidth, y);
                  doc.setFont('helvetica', 'normal');
                  y += 12;

                  // Draw amount box (styled)
                  const boxResult = this.pdfReceiptService.drawAmountBox(doc, montantDisplay, pageWidth, margin, y);
                  y = boxResult.boxY + boxResult.boxH + 12;

                  // Amount in words (wrapped, auto-shrink)
                  y = this.pdfReceiptService.drawAmountInWords(doc, montantTxt, leftX, labelWidth, pageWidth, margin, y);

                  // Details
                  doc.setFontSize(11);
                  doc.setFont('helvetica', 'normal');
                  y += 12;
                  doc.text('Pour :', leftX, y);
                  const fieldWidth = pageWidth - margin * 2 - labelWidth - 60;
                  doc.text(text, leftX + labelWidth, y, { maxWidth: fieldWidth });
                  y += 36;

                  // Footer with signature and agency code
                  this.pdfReceiptService.drawFooter(doc, leftX, labelWidth, pageWidth, pageHeight, margin, agenceCode, y);

                  const fileName = `recu_${id}.pdf`;
                  doc.save(fileName);
                  return;
                } catch (e) {
                  console.warn('Génération PDF client échouée, ouverture simulation', e);
                  const html = `<html><body><h1>Reçu (simulation)</h1><p>ID: ${id}</p><p>Message: ${text}</p></body></html>`;
                  const blob = new Blob([html], { type: 'text/html' });
                  const url = URL.createObjectURL(blob);
                  window.open(url, '_blank');
                  return;
                }
              }
              alert('Reçu généré (serveur): ' + text);
              return;
            }
          } catch (e) {
            console.warn('Traitement réponse reçu failed', e);
          }
          alert('Reçu généré (voir serveur)');
        },
        error: (err) => {
          console.error('Erreur génération reçu', { id, err });
          alert('Erreur lors de la génération du reçu (voir console pour détails)');
        }
      });
    } catch (e) {
      console.error('Erreur lors de l appel generateReceipt', e);
    }
  }

  // Helpers
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