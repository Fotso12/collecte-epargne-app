import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParametresService } from '../../../../core/services/parametres.service';
import { TypeCompteDto } from '../../../../donnees/modeles/parametres.modele';

@Component({
    selector: 'app-gestion-types-compte',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="card-body p-0">
        <div class="d-flex justify-content-between align-items-center p-3 bg-light border-bottom">
            <h5 class="m-0 fw-bold text-primary">Gestion des Types de Compte</h5>
            <button class="btn btn-primary btn-sm rounded-pill px-3" (click)="openModal()">
                <i class="fas fa-plus me-2"></i>Nouveau Type
            </button>
        </div>
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light">
            <tr>
              <th class="ps-4">Code</th>
              <th>Nom</th>
              <th>Taux</th>
              <th>Solde Min</th>
              <th>Frais</th>
              <th class="text-end pe-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let t of types">
              <td class="ps-4 fw-bold">{{ t.code }}</td>
              <td>{{ t.nom }}</td>
              <td>{{ t.tauxInteret }}%</td>
              <td>{{ t.soldeMinimum | number }}</td>
              <td>{{ t.fraisOuverture | number }} (O) / {{ t.fraisCloture | number }} (C)</td>
              <td class="text-end pe-4">
                <button class="btn btn-sm btn-light text-info me-2" (click)="viewDetails(t)" title="Voir détails"><i class="fas fa-eye"></i></button>
                <button class="btn btn-sm btn-light text-primary me-2" (click)="openModal(t)" title="Modifier"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-light text-danger" (click)="confirmDelete(t)" title="Supprimer"><i class="fas fa-trash"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL -->
    <div class="modal-backdrop-custom" *ngIf="isModalOpen"></div>
    <div class="modal-wrapper-custom" *ngIf="isModalOpen" style="max-width: 700px;">
      <div class="modal-content-custom p-4 shadow-lg border-0">
        <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
            <h5 class="fw-bold m-0 text-dark">{{ isEdit ? 'Modifier' : 'Nouveau' }} Type de Compte</h5>
            <button class="btn-close" (click)="closeModal()"></button>
        </div>
        <form [formGroup]="form" (ngSubmit)="save()">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label small text-muted">Code</label>
                    <input type="text" class="form-control" formControlName="code">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Nom</label>
                    <input type="text" class="form-control" formControlName="nom">
                </div>
                <div class="col-12">
                     <label class="form-label small text-muted">Description</label>
                     <textarea class="form-control" formControlName="description" rows="2"></textarea>
                </div>
                <div class="col-md-4">
                    <label class="form-label small text-muted">Taux Intérêt (%)</label>
                    <input type="number" step="0.01" class="form-control" formControlName="tauxInteret">
                </div>
                <div class="col-md-4">
                    <label class="form-label small text-muted">Solde Minimum</label>
                    <input type="number" class="form-control" formControlName="soldeMinimum">
                </div>
                <div class="col-md-4">
                    <label class="form-label small text-muted">Blocage (Jours)</label>
                    <input type="number" class="form-control" formControlName="dureeBlocageJours">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Frais Ouverture</label>
                    <input type="number" class="form-control" formControlName="fraisOuverture">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Frais Clôture</label>
                    <input type="number" class="form-control" formControlName="fraisCloture">
                </div>
                <div class="col-12">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" formControlName="autoriserRetrait" id="retraitCheck">
                        <label class="form-check-label" for="retraitCheck">Autoriser les retraits</label>
                    </div>
                </div>
            </div>
            <div class="d-flex gap-2 mt-4">
                <button type="button" class="btn btn-light w-100 rounded-pill" (click)="closeModal()">Annuler</button>
                <button type="submit" class="btn btn-primary w-100 rounded-pill" [disabled]="form.invalid">Enregistrer</button>
            </div>
        </form>
      </div>
    </div>

    <!-- MODAL CONFIRMATION -->
    <div class="modal-backdrop-custom" *ngIf="showConfirmModal"></div>
    <div class="modal-wrapper-custom" *ngIf="showConfirmModal" style="max-width: 400px;">
      <div class="modal-content-custom p-4 shadow-lg border-0">
        <div class="text-center mb-3">
          <i class="fas fa-question-circle text-warning" style="font-size: 3rem;"></i>
        </div>
        <h5 class="text-center mb-3">{{ confirmMessage }}</h5>
        <div class="d-flex gap-2">
          <button class="btn btn-light w-100 rounded-pill" (click)="cancelConfirm()">Annuler</button>
          <button class="btn btn-primary w-100 rounded-pill" (click)="proceedConfirm()">Confirmer</button>
        </div>
      </div>
    </div>

    <!-- MODAL DETAILS -->
    <div class="modal-backdrop-custom" *ngIf="showDetailsModal"></div>
    <div class="modal-wrapper-custom" *ngIf="showDetailsModal" style="max-width: 600px;">
      <div class="modal-content-custom p-4 shadow-lg border-0">
        <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
          <h5 class="fw-bold m-0 text-dark">Détails du Type de Compte</h5>
          <button class="btn-close" (click)="closeDetails()"></button>
        </div>
        <div class="row g-3" *ngIf="selectedItem">
          <div class="col-md-6">
            <label class="small text-muted">Code</label>
            <p class="fw-bold">{{ selectedItem.code }}</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Nom</label>
            <p class="fw-bold">{{ selectedItem.nom }}</p>
          </div>
          <div class="col-12">
            <label class="small text-muted">Description</label>
            <p>{{ selectedItem.description || 'N/A' }}</p>
          </div>
          <div class="col-md-4">
            <label class="small text-muted">Taux Intérêt</label>
            <p class="badge bg-success-subtle text-success">{{ selectedItem.tauxInteret }}%</p>
          </div>
          <div class="col-md-4">
            <label class="small text-muted">Solde Minimum</label>
            <p class="badge bg-info-subtle text-info">{{ selectedItem.soldeMinimum | number }} FCFA</p>
          </div>
          <div class="col-md-4">
            <label class="small text-muted">Blocage</label>
            <p class="badge bg-warning-subtle text-warning">{{ selectedItem.dureeBlocageJours }} jours</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Frais Ouverture</label>
            <p>{{ selectedItem.fraisOuverture | number }} FCFA</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Frais Clôture</label>
            <p>{{ selectedItem.fraisCloture | number }} FCFA</p>
          </div>
          <div class="col-12">
            <label class="small text-muted">Autoriser Retrait</label>
            <p><span class="badge" [ngClass]="selectedItem.autoriserRetrait ? 'bg-success' : 'bg-danger'">{{ selectedItem.autoriserRetrait ? 'Oui' : 'Non' }}</span></p>
          </div>
        </div>
        <button class="btn btn-light w-100 rounded-pill mt-3" (click)="closeDetails()">Fermer</button>
      </div>
    </div>
  `,
    styleUrls: ['./parametres.css']
})
export class GestionTypeComptesComponent implements OnInit {
    types: TypeCompteDto[] = [];
    isModalOpen = false;
    isEdit = false;
    form: FormGroup;
    showConfirmModal = false;
    showDetailsModal = false;
    confirmMessage = '';
    confirmAction: (() => void) | null = null;
    selectedItem: TypeCompteDto | null = null;

    constructor(
        private paramService: ParametresService,
        private fb: FormBuilder,
        private cdr: ChangeDetectorRef
    ) {
        this.form = this.fb.group({
            id: [],
            code: ['', Validators.required],
            nom: ['', Validators.required],
            description: [''],
            tauxInteret: [0],
            soldeMinimum: [0],
            fraisOuverture: [0],
            fraisCloture: [0],
            autoriserRetrait: [true],
            dureeBlocageJours: [0]
        });
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.paramService.getTypeComptes().subscribe(data => {
            this.types = data;
            this.cdr.detectChanges();
        });
    }

    openModal(item: any = null) {
        this.isEdit = !!item;
        if (item) {
            this.form.patchValue(item);
        } else {
            this.form.reset({
                tauxInteret: 0, soldeMinimum: 0, fraisOuverture: 0, fraisCloture: 0,
                autoriserRetrait: true, dureeBlocageJours: 0
            });
        }
        this.isModalOpen = true;
    }

    closeModal() {
        this.isModalOpen = false;
    }

    save() {
        if (this.form.invalid) return;
        const formValue = this.form.value;

        this.confirmMessage = this.isEdit ? 'Confirmer la modification ?' : 'Confirmer la création ?';
        this.confirmAction = () => {
            if (this.isEdit && formValue.id) {
                this.paramService.updateTypeCompte(formValue.id, formValue).subscribe(() => {
                    this.closeModal();
                    this.loadData();
                });
            } else {
                this.paramService.saveTypeCompte(formValue).subscribe(() => {
                    this.closeModal();
                    this.loadData();
                });
            }
        };
        this.showConfirmModal = true;
    }

    confirmDelete(item: TypeCompteDto) {
        this.confirmMessage = `Supprimer "${item.nom}" ?`;
        this.confirmAction = () => {
            if (item.id) {
                this.paramService.deleteTypeCompte(item.id).subscribe(() => this.loadData());
            }
        };
        this.showConfirmModal = true;
    }

    proceedConfirm() {
        if (this.confirmAction) {
            this.confirmAction();
        }
        this.showConfirmModal = false;
        this.confirmAction = null;
    }

    cancelConfirm() {
        this.showConfirmModal = false;
        this.confirmAction = null;
    }

    viewDetails(item: TypeCompteDto) {
        this.selectedItem = item;
        this.showDetailsModal = true;
    }

    closeDetails() {
        this.showDetailsModal = false;
        this.selectedItem = null;
    }
}
