import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParametresService } from '../../../../core/services/parametres.service';
import { RoleDto } from '../../../../donnees/modeles/parametres.modele';

@Component({
    selector: 'app-gestion-roles',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="card-body p-0">
        <div class="d-flex justify-content-between align-items-center p-3 bg-light border-bottom">
            <h5 class="m-0 fw-bold text-primary">Gestion des Rôles</h5>
            <button class="btn btn-primary btn-sm rounded-pill px-3" (click)="openModal()">
                <i class="fas fa-plus me-2"></i>Nouveau Rôle
            </button>
        </div>
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light">
            <tr>
              <th class="ps-4">Code</th>
              <th>Nom</th>
              <th>Description</th>
              <th class="text-end pe-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let r of roles">
              <td class="ps-4 fw-bold">{{ r.code }}</td>
              <td>{{ r.nom }}</td>
              <td class="text-muted small">{{ r.description }}</td>
              <td class="text-end pe-4">
                <button class="btn btn-sm btn-light text-info me-2" (click)="viewDetails(r)" title="Voir détails"><i class="fas fa-eye"></i></button>
                <button class="btn btn-sm btn-light text-primary me-2" (click)="openModal(r)" title="Modifier"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-light text-danger" (click)="confirmDelete(r)" title="Supprimer"><i class="fas fa-trash"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL EDITION -->
    <div class="modal-backdrop-custom" *ngIf="isModalOpen"></div>
    <div class="modal-wrapper-custom" *ngIf="isModalOpen">
      <div class="modal-content-custom p-4 shadow-lg border-0">
        <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
            <h5 class="fw-bold m-0 text-dark">{{ isEdit ? 'Modifier' : 'Nouveau' }} Rôle</h5>
            <button class="btn-close" (click)="closeModal()"></button>
        </div>
        <form [formGroup]="form" (ngSubmit)="save()">
            <div class="mb-3">
                <label class="form-label small text-muted">Code</label>
                <input type="text" class="form-control" formControlName="code">
            </div>
            <div class="mb-3">
                <label class="form-label small text-muted">Nom</label>
                <input type="text" class="form-control" formControlName="nom">
            </div>
            <div class="mb-3">
                <label class="form-label small text-muted">Description</label>
                <textarea class="form-control" formControlName="description"></textarea>
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
    <div class="modal-wrapper-custom" *ngIf="showDetailsModal" style="max-width: 500px;">
      <div class="modal-content-custom p-4 shadow-lg border-0">
        <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
          <h5 class="fw-bold m-0 text-dark">Détails du Rôle</h5>
          <button class="btn-close" (click)="closeDetails()"></button>
        </div>
        <div class="row g-3" *ngIf="selectedItem">
          <div class="col-12">
            <label class="small text-muted">Code</label>
            <p class="fw-bold">{{ selectedItem.code }}</p>
          </div>
          <div class="col-12">
            <label class="small text-muted">Nom</label>
            <p class="fw-bold">{{ selectedItem.nom }}</p>
          </div>
          <div class="col-12">
            <label class="small text-muted">Description</label>
            <p>{{ selectedItem.description || 'N/A' }}</p>
          </div>
        </div>
        <button class="btn btn-light w-100 rounded-pill mt-3" (click)="closeDetails()">Fermer</button>
      </div>
    </div>
  `,
    styleUrls: ['./parametres.css']
})
export class GestionRolesComponent implements OnInit {
    roles: RoleDto[] = [];
    isModalOpen = false;
    isEdit = false;
    form: FormGroup;
    showConfirmModal = false;
    showDetailsModal = false;
    confirmMessage = '';
    confirmAction: (() => void) | null = null;
    selectedItem: RoleDto | null = null;

    constructor(
        private paramService: ParametresService,
        private fb: FormBuilder,
        private cdr: ChangeDetectorRef
    ) {
        this.form = this.fb.group({
            id: [],
            code: ['', Validators.required],
            nom: ['', Validators.required],
            description: ['']
        });
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.paramService.getRoles().subscribe(data => {
            this.roles = data;
            this.cdr.detectChanges();
        });
    }

    openModal(item: any = null) {
        this.isEdit = !!item;
        if (item) {
            this.form.patchValue(item);
        } else {
            this.form.reset();
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
                this.paramService.updateRole(formValue.id, formValue).subscribe(() => {
                    this.closeModal();
                    this.loadData();
                });
            } else {
                this.paramService.saveRole(formValue).subscribe(() => {
                    this.closeModal();
                    this.loadData();
                });
            }
        };
        this.showConfirmModal = true;
    }

    confirmDelete(item: RoleDto) {
        this.confirmMessage = `Supprimer "${item.nom}" ?`;
        this.confirmAction = () => {
            if (item.id) {
                this.paramService.deleteRole(item.id).subscribe(() => this.loadData());
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

    viewDetails(item: RoleDto) {
        this.selectedItem = item;
        this.showDetailsModal = true;
    }

    closeDetails() {
        this.showDetailsModal = false;
        this.selectedItem = null;
    }
}
