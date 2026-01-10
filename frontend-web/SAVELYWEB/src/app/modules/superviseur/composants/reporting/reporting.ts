import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportingService } from '../../../../core/services/reporting.service';

@Component({
    selector: 'app-reporting',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './reporting.html',
    styleUrls: ['./reporting.css']
})
export class ReportingComponent {
    dateDebut: string = '';
    dateFin: string = '';
    transactions: any[] = [];
    chargement: boolean = false;
    erreur: string = '';

    // Stats du rapport
    totalDepot: number = 0;
    totalRetrait: number = 0;
    volumeTotal: number = 0;

    constructor(private reportingService: ReportingService) {
        const today = new Date();
        this.dateFin = today.toISOString().split('T')[0];
        const sevenDaysAgo = new Date();
        sevenDaysAgo.setDate(today.getDate() - 7);
        this.dateDebut = sevenDaysAgo.toISOString().split('T')[0];
    }

    genererRapport() {
        this.chargement = true;
        this.erreur = '';
        this.transactions = [];

        this.reportingService.getTransactions(this.dateDebut, this.dateFin).subscribe({
            next: (data) => {
                this.transactions = data;
                this.calculerStats();
                this.chargement = false;
            },
            error: (err) => {
                console.error("Erreur chargement rapport", err);
                this.erreur = "Impossible de récupérer les données du rapport.";
                this.chargement = false;
            }
        });
    }

    calculerStats() {
        this.totalDepot = this.transactions
            .filter(t => t.typeTransaction === 'DEPOT' && t.statut === 'TERMINEE')
            .reduce((acc, t) => acc + t.montant, 0);

        this.totalRetrait = this.transactions
            .filter(t => t.typeTransaction === 'RETRAIT' && t.statut === 'TERMINEE')
            .reduce((acc, t) => acc + t.montant, 0);

        this.volumeTotal = this.totalDepot + this.totalRetrait;
    }

    exporterCSV() {
        // Logique basique d'export CSV
        if (this.transactions.length === 0) return;

        const headers = ['Reference', 'Date', 'Type', 'Montant', 'Statut', 'Initiateur'];
        const rows = this.transactions.map(t => [
            t.reference,
            t.dateTransaction,
            t.typeTransaction,
            t.montant,
            t.statut,
            t.nomInitiateur
        ]);

        let csvContent = "data:text/csv;charset=utf-8,"
            + headers.join(",") + "\n"
            + rows.map(e => e.join(",")).join("\n");

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", `rapport_${this.dateDebut}_${this.dateFin}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}
