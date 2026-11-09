import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CaissierService } from '../../../../core/services/caissier.service';

@Component({
  selector: 'app-caissier-validations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './validations.html',
  styleUrl: './validations.css'
})
export class CaissierValidations implements OnInit {
  transactions: any[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';

  // Modal state
  showValidateModal = false;
  showRejectModal = false;
  selectedTransaction: any = null;
  rejectMotif = '';

  constructor(
    private caissierService: CaissierService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerTransactions();
  }

  chargerTransactions(): void {
    this.isLoading = true;
    this.caissierService.getTransactionsPending().subscribe({
      next: (data: any) => {
        this.transactions = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.errorMessage = 'Erreur lors du chargement des transactions';
        console.error(err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Ouvrir modal de validation
  openValidateModal(transaction: any): void {
    this.selectedTransaction = transaction;
    this.showValidateModal = true;
  }

  // Ouvrir modal de rejet
  openRejectModal(transaction: any): void {
    this.selectedTransaction = transaction;
    this.rejectMotif = '';
    this.showRejectModal = true;
  }

  // Fermer modals
  closeModals(): void {
    this.showValidateModal = false;
    this.showRejectModal = false;
    this.selectedTransaction = null;
    this.rejectMotif = '';
  }

  // Confirmer validation
  confirmValidate(): void {
    if (!this.selectedTransaction) return;

    this.caissierService.validateTransaction(this.selectedTransaction.id).subscribe({
      next: (res: any) => {
        this.successMessage = 'Transaction validée avec succès';
        this.closeModals();
        this.chargerTransactions();
        this.cdr.detectChanges();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.errorMessage = 'Erreur lors de la validation';
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }

  // Confirmer rejet
  confirmReject(): void {
    if (!this.selectedTransaction || !this.rejectMotif.trim()) {
      this.errorMessage = 'Veuillez entrer un motif de rejet';
      return;
    }

    this.caissierService.rejectTransaction(this.selectedTransaction.id, this.rejectMotif).subscribe({
      next: (res: any) => {
        this.successMessage = 'Transaction rejetée avec succès';
        this.closeModals();
        this.chargerTransactions();
        this.cdr.detectChanges();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.errorMessage = 'Erreur lors du rejet';
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }

  // Format montant
  formatMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XAF'
    }).format(montant);
  }
}
