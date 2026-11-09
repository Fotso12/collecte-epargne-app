import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParametresService } from '../../../../core/services/parametres.service';
import { RoleDto, TypeCompteDto, PlanCotisationDto, NotificationDto } from '../../../../donnees/modeles/parametres.modele';

import { GestionRolesComponent } from './gestion-roles';
import { GestionTypeComptesComponent } from './gestion-types-compte';
import { GestionPlansComponent } from './gestion-plans';
import { GestionNotificationsComponent } from './gestion-notifications';

@Component({
    selector: 'app-parametres',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        GestionRolesComponent,
        GestionTypeComptesComponent,
        GestionPlansComponent,
        GestionNotificationsComponent
    ],
    templateUrl: './parametres.html',
    styleUrls: ['./parametres.css']
})
export class ParametresComponent implements OnInit {
    activeTab = 'ROLES'; // ROLES, TYPES, PLANS, NOTIFS

    roles: RoleDto[] = [];
    typesCompte: TypeCompteDto[] = [];
    plans: PlanCotisationDto[] = [];
    notifications: NotificationDto[] = [];

    isModalOpen = false;
    currentForm!: FormGroup;
    modalTitle = '';
    isEdit = false;

    constructor(
        private paramService: ParametresService,
        private fb: FormBuilder,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadData();
    }

    setTab(tab: string) {
        this.activeTab = tab;
        this.loadData();
    }

    loadData() {
        switch (this.activeTab) {
            case 'ROLES':
                this.paramService.getRoles().subscribe(data => { this.roles = data; this.cdr.detectChanges(); });
                break;
            case 'TYPES':
                this.paramService.getTypeComptes().subscribe(data => { this.typesCompte = data; this.cdr.detectChanges(); });
                break;
            case 'PLANS':
                this.paramService.getPlans().subscribe(data => { this.plans = data; this.cdr.detectChanges(); });
                break;
            case 'NOTIFS':
                this.paramService.getNotifications().subscribe(data => { this.notifications = data; this.cdr.detectChanges(); });
                break;
        }
    }

    openModal(item: any = null) {
        this.isEdit = !!item;
        this.isModalOpen = true;

        if (this.activeTab === 'ROLES') {
            this.modalTitle = item ? 'Modifier Rôle' : 'Nouveau Rôle';
            this.currentForm = this.fb.group({
                idRole: [item?.idRole],
                code: [item?.code || '', Validators.required],
                libelle: [item?.libelle || '', Validators.required]
            });
        } else if (this.activeTab === 'TYPES') {
            this.modalTitle = item ? 'Modifier Type Compte' : 'Nouveau Type Compte';
            this.currentForm = this.fb.group({
                idTypeCompte: [item?.idTypeCompte],
                code: [item?.code || '', Validators.required],
                libelle: [item?.libelle || '', Validators.required]
            });
        } else if (this.activeTab === 'PLANS') {
            this.modalTitle = item ? 'Modifier Plan' : 'Nouveau Plan';
            this.currentForm = this.fb.group({
                idPlan: [item?.idPlan], // String ID
                libelle: [item?.libelle || '', Validators.required],
                description: [item?.description || ''],
                montantJour: [item?.montantJour || 0, [Validators.required, Validators.min(0)]]
            });
        } else if (this.activeTab === 'NOTIFS') {
            this.modalTitle = 'Nouvelle Notification';
            this.currentForm = this.fb.group({
                titre: ['', Validators.required],
                message: ['', Validators.required],
                type: ['INFO']
            });
        }
    }

    closeModal() {
        this.isModalOpen = false;
    }

    save() {
        if (this.currentForm.invalid) return;
        const val = this.currentForm.value;

        if (this.activeTab === 'ROLES') {
            this.paramService.saveRole(val).subscribe(() => { this.closeModal(); this.loadData(); });
        } else if (this.activeTab === 'TYPES') {
            this.paramService.saveTypeCompte(val).subscribe(() => { this.closeModal(); this.loadData(); });
        } else if (this.activeTab === 'PLANS') {
            this.paramService.savePlan(val).subscribe(() => { this.closeModal(); this.loadData(); });
        } else if (this.activeTab === 'NOTIFS') {
            this.paramService.sendNotification(val).subscribe(() => { this.closeModal(); this.loadData(); });
        }
    }

    deleteItem(id: any) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) return;

        if (this.activeTab === 'ROLES') {
            this.paramService.deleteRole(id).subscribe(() => this.loadData());
        } else if (this.activeTab === 'TYPES') {
            this.paramService.deleteTypeCompte(id).subscribe(() => this.loadData());
        } else if (this.activeTab === 'PLANS') {
            this.paramService.deletePlan(id).subscribe(() => this.loadData());
        }
    }
}
