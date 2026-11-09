import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CollecteurStats {
    matricule: string;
    nom: string;
    prenom: string;
    montantCollecte: number;
    nombreClients: number;
    nombreTransactions: number;
}

export interface ClientRetard {
    codeClient: string;
    nom: string;
    prenom: string;
    telephone: string;
    joursRetard: number;
    montantDu: number;
    interetsRetard: number;
}

export interface SuperviseurKpi {
    meilleurCollecteur: CollecteurStats;
    collecteurPlusClients: CollecteurStats;
    collecteJournaliere: number;
    collecteHebdomadaire: number;
    collecteMensuelle: number;
    caisseEntreprise: number;
    gainSuperviseur: number;
    clientsEnRetard: ClientRetard[];
    nombreTransactions: number;
    nombreClientsActifs: number;
}

@Injectable({
    providedIn: 'root'
})
export class KpiService {
    private apiUrl = `${environment.apiUrl}/api/superviseur`;

    constructor(private http: HttpClient) { }

    /**
     * Récupère tous les KPIs du superviseur
     */
    getKpis(): Observable<SuperviseurKpi> {
        return this.http.get<SuperviseurKpi>(`${this.apiUrl}/kpis`);
    }

    /**
     * Formate un montant en FCFA
     */
    formatMontant(montant: number): string {
        if (!montant) return '0 FCFA';
        return new Intl.NumberFormat('fr-FR', {
            style: 'decimal',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(montant) + ' FCFA';
    }

    /**
     * Calcule le pourcentage de variation
     */
    calculateVariation(current: number, previous: number): number {
        if (previous === 0) return 0;
        return ((current - previous) / previous) * 100;
    }

    /**
     * Retourne la couleur du statut de transaction
     */
    getStatusColor(status: string): string {
        const colors: { [key: string]: string } = {
            'VALIDEE': '#28a745',
            'EN_ATTENTE': '#ffc107',
            'REJETEE': '#dc3545',
            'ANNULEE': '#6c757d'
        };
        return colors[status] || '#6c757d';
    }

    /**
     * Retourne le libellé du statut
     */
    getStatusLabel(status: string): string {
        const labels: { [key: string]: string } = {
            'VALIDEE': 'Validée',
            'EN_ATTENTE': 'En attente',
            'REJETEE': 'Rejetée',
            'ANNULEE': 'Annulée'
        };
        return labels[status] || status;
    }
}
