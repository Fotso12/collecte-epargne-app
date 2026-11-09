import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParametresService } from '../../../../core/services/parametres.service';
import { PlanCotisationDto } from '../../../../donnees/modeles/parametres.modele';

@Component({
    selector: 'app-gestion-plans',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="card-body p-0">
        <div class="d-flex justify-content-between align-items-center p-3 bg-light border-bottom">
            <h5 class="m-0 fw-bold text-primary">Plans de Cotisation</h5>
            <button class="btn btn-primary btn-sm rounded-pill px-3" (click)="openModal()">
                <i class="fas fa-plus me-2"></i>Nouveau Plan
            </button>
        </div>
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light">
            <tr>
              <th class="ps-4">Nom</th>
              <th>Fréquence</th>
              <th>Montant Attendu</th>
              <th>Période</th>
              <th>Statut</th>
              <th class="text-end pe-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let p of plans">
              <td class="ps-4 fw-bold">{{ p.nom }}</td>
              <td><span class="badge bg-secondary">{{ p.frequence }}</span></td>
              <td><span class="badge bg-success-subtle text-success">{{ p.montantAttendu | number }} FCFA</span></td>
              <td class="small text-muted">{{ p.dateDebut }} <i class="fas fa-arrow-right mx-1"></i> {{ p.dateFin || '...' }}</td>
              <td><span class="badge" [ngClass]="p.statut === 'ACTIF' ? 'bg-success' : 'bg-warning'">{{ p.statut }}</span></td>
              <td class="text-end pe-4">
                <button class="btn btn-sm btn-light text-info me-2" (click)="viewDetails(p)" title="Voir détails"><i class="fas fa-eye"></i></button>
                <button class="btn btn-sm btn-light text-primary me-2" (click)="openModal(p)" title="Modifier"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-light text-danger" (click)="confirmDelete(p)" title="Supprimer"><i class="fas fa-trash"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL EDITION -->
    <div class="modal-backdrop-custom" *ngIf="isModalOpen"></div>
    <div class="modal-wrapper-custom" *ngIf="isModalOpen" style="max-width: 600px;">
      <div class="modal-content-custom p-4 shadow-lg border-0">
        <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
            <h5 class="fw-bold m-0 text-dark">{{ isEdit ? 'Modifier' : 'Nouveau' }} Plan</h5>
            <button class="btn-close" (click)="closeModal()"></button>
        </div>
        <form [formGroup]="form" (ngSubmit)="save()">
            <div class="row g-3">
                <div class="col-12">
                    <label class="form-label small text-muted">Nom du Plan</label>
                    <input type="text" class="form-control" formControlName="nom">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Fréquence</label>
                    <select class="form-select" formControlName="frequence">
                        <option value="QUOTIDIEN">Quotidien</option>
                        <option value="HEBDOMADAIRE">Hebdomadaire</option>
                        <option value="MENSUEL">Mensuel</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Montant Attendu</label>
                    <input type="number" class="form-control" formControlName="montantAttendu">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Date Début</label>
                    <input type="date" class="form-control" formControlName="dateDebut">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Date Fin</label>
                    <input type="date" class="form-control" formControlName="dateFin">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Durée (Jours)</label>
                    <input type="number" class="form-control" formControlName="dureeJours">
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Pénalité Retard (%)</label>
                    <input type="number" step="0.01" class="form-control" formControlName="tauxPenaliteRetard">
                </div>
                 <div class="col-12">
                    <label class="form-label small text-muted">Statut</label>
                    <select class="form-select" formControlName="statut">
                        <option value="ACTIF">Actif</option>
                        <option value="SUSPENDU">Suspendu</option>
                        <option value="TERMINE">Terminé</option>
                    </select>
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
          <h5 class="fw-bold m-0 text-dark">Détails du Plan de Cotisation</h5>
          <button class="btn-close" (click)="closeDetails()"></button>
        </div>
        <div class="row g-3" *ngIf="selectedItem">
          <div class="col-12">
            <label class="small text-muted">Nom du Plan</label>
            <p class="fw-bold">{{ selectedItem.nom }}</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Fréquence</label>
            <p><span class="badge bg-secondary">{{ selectedItem.frequence }}</span></p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Statut</label>
            <p><span class="badge" [ngClass]="selectedItem.statut === 'ACTIF' ? 'bg-success' : 'bg-warning'">{{ selectedItem.statut }}</span></p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Montant Attendu</label>
            <p class="badge bg-success-subtle text-success">{{ selectedItem.montantAttendu | number }} FCFA</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Durée</label>
            <p>{{ selectedItem.dureeJours }} jours</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Date Début</label>
            <p>{{ selectedItem.dateDebut }}</p>
          </div>
          <div class="col-md-6">
            <label class="small text-muted">Date Fin</label>
            <p>{{ selectedItem.dateFin || 'N/A' }}</p>
          </div>
          <div class="col-12">
            <label class="small text-muted">Taux Pénalité Retard</label>
            <p>{{ selectedItem.tauxPenaliteRetard }}%</p>
          </div>
        </div>
        <button class="btn btn-light w-100 rounded-pill mt-3" (click)="closeDetails()">Fermer</button>
      </div>
    </div>
  `,
    styleUrls: ['./parametres.css']
})
export class GestionPlansComponent implements OnInit {
    plans: PlanCotisationDto[] = [];
    isModalOpen = false;
    isEdit = false;
    form: FormGroup;
    showConfirmModal = false;
    showDetailsModal = false;
    confirmMessage = '';
    confirmAction: (() => void) | null = null;
    selectedItem: PlanCotisationDto | null = null;

    constructor(
        private paramService: ParametresService,
        private fb: FormBuilder,
        private cdr: ChangeDetectorRef
    ) {
        this.form = this.fb.group({
            idPlan: [],
            nom: ['', Validators.required],
            frequence: ['QUOTIDIEN', Validators.required],
            montantAttendu: [0, Validators.required],
            dateDebut: [new Date().toISOString().split('T')[0], Validators.required],
            dateFin: [],
            dureeJours: [30],
            tauxPenaliteRetard: [0],
            statut: ['ACTIF']
        });
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.paramService.getPlans().subscribe(data => {
            this.plans = data;
            this.cdr.detectChanges();
        });
    }

    openModal(item: any = null) {
        this.isEdit = !!item;
        if (item) {
            this.form.patchValue(item);
        } else {
            this.form.reset({
                frequence: 'QUOTIDIEN', montantAttendu: 0,
                dateDebut: new Date().toISOString().split('T')[0],
                dureeJours: 365, tauxPenaliteRetard: 0, statut: 'ACTIF'
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
            if (this.isEdit && formValue.idPlan) {
                this.paramService.updatePlan(formValue.idPlan, formValue).subscribe(() => {
                    this.closeModal();
                    this.loadData();
                });
            } else {
                this.paramService.savePlan(formValue).subscribe(() => {
                    this.closeModal();
                    this.loadData();
                });
            }
        };
        this.showConfirmModal = true;
    }

    confirmDelete(item: PlanCotisationDto) {
        this.confirmMessage = `Supprimer "${item.nom}" ?`;
        this.confirmAction = () => {
            if (item.idPlan) {
                this.paramService.deletePlan(item.idPlan).subscribe(() => this.loadData());
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

    viewDetails(item: PlanCotisationDto) {
        this.selectedItem = item;
        this.showDetailsModal = true;
    }

    closeDetails() {
        this.showDetailsModal = false;
        this.selectedItem = null;
    }
}
