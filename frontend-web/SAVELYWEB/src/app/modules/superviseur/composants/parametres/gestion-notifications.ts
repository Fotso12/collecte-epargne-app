import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParametresService } from '../../../../core/services/parametres.service';
import { NotificationDto } from '../../../../donnees/modeles/parametres.modele';

@Component({
    selector: 'app-gestion-notifications',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    template: `
        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
            <div class="card-body p-0">
                <div class="d-flex justify-content-between align-items-center p-3 bg-light border-bottom">
                    <h5 class="m-0 fw-bold text-primary">Gestion des Notifications</h5>
                    <button class="btn btn-primary btn-sm rounded-pill px-3" (click)="openModal()">
                        <i class="fas fa-paper-plane me-2"></i>Envoyer Notification
                    </button>
                </div>
                <table class="table table-hover align-middle mb-0">
                    <thead class="bg-light">
                        <tr>
                            <th class="ps-4">Titre</th>
                            <th>Message</th>
                            <th>Type / Catégorie</th>
                            <th>Date Envoi</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr *ngFor="let n of notifications">
                            <td class="ps-4 fw-bold">{{ n.titre }}</td>
                            <td>{{ n.message }}</td>
                            <td>
                                <span class="badge bg-secondary me-1">{{ n.type }}</span>
                                <span class="badge bg-light text-dark border">{{ n.categorie }}</span>
                            </td>
                            <td class="text-muted small">{{ n.dateEnvoi | date: 'medium' }}</td>
                            <td><span class="badge bg-info-subtle text-info">{{ n.statut }}</span></td>
                        </tr>
                        <tr *ngIf="notifications.length === 0">
                            <td colspan="5" class="text-center py-4 text-muted">Aucune notification trouvée</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- MODAL -->
        <div class="modal-backdrop-custom" *ngIf="isModalOpen"></div>
        <div class="modal-wrapper-custom" *ngIf="isModalOpen">
            <div class="modal-content-custom p-4 shadow-lg border-0">
                <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                    <h5 class="fw-bold m-0 text-dark">Nouvelle Notification</h5>
                    <button class="btn-close" (click)="closeModal()"></button>
                </div>
                <form [formGroup]="form" (ngSubmit)="save()">
                    <div class="mb-3">
                        <label class="form-label small text-muted">Titre</label>
                        <input type="text" class="form-control" formControlName="titre">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small text-muted">Message</label>
                        <textarea class="form-control" formControlName="message" rows="3"></textarea>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small text-muted">Type</label>
                            <select class="form-select" formControlName="type">
                                <option value="SMS">SMS</option>
                                <option value="EMAIL">Email</option>
                                <option value="PUSH">Push</option>
                                <option value="IN_APP">In-App</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small text-muted">Catégorie</label>
                            <select class="form-select" formControlName="categorie">
                                <option value="TRANSACTION">Transaction</option>
                                <option value="RAPPEL_COTISATION">Rappel</option>
                                <option value="ALERTE">Alerte</option>
                                <option value="PROMO">Promo</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label small text-muted">Cible (Code Client ou 'TOUS')</label>
                            <input type="text" class="form-control" formControlName="codeClient" placeholder="Laisser vide pour tous">
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                        <button type="button" class="btn btn-light w-100 rounded-pill" (click)="closeModal()">Annuler</button>
                        <button type="submit" class="btn btn-primary w-100 rounded-pill" [disabled]="form.invalid">Envoyer</button>
                    </div>
                </form>
            </div>
        </div>
    `,
    styleUrls: ['./parametres.css']
})
export class GestionNotificationsComponent implements OnInit {
    notifications: NotificationDto[] = [];
    isModalOpen = false;
    form: FormGroup;

    constructor(
        private paramService: ParametresService,
        private fb: FormBuilder,
        private cdr: ChangeDetectorRef
    ) {
        this.form = this.fb.group({
            titre: ['', Validators.required],
            message: ['', Validators.required],
            type: ['IN_APP', Validators.required],
            categorie: ['ALERTE', Validators.required],
            codeClient: [''],
            statut: ['SENT']
        });
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.paramService.getNotifications().subscribe(data => {
            this.notifications = data;
            this.cdr.detectChanges();
        });
    }

    openModal() {
        this.form.reset({ type: 'IN_APP', categorie: 'ALERTE', statut: 'SENT' });
        this.isModalOpen = true;
    }

    closeModal() {
        this.isModalOpen = false;
    }

    save() {
        if (this.form.invalid) return;
        this.paramService.sendNotification(this.form.value).subscribe(() => {
            this.closeModal();
            this.loadData();
        });
    }
}
