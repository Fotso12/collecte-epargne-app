package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long totalClients;
    private long totalCollecteurs;
    private long totalCaissiers;
    private BigDecimal volumeCotisation;
    private double pourcentageTransactionsValidees;
    
    // Nouveaux KPIs
    private long totalComptesActifs;
    private BigDecimal soldeTotalEpargne;
    private BigDecimal volumeRetraits;
    private long transactionsEnAttente;
    private double tauxPenalites;
    private BigDecimal epargneParClient;

    // Nouveaux KPIs demandés
    private String nomCollecteurPlusClients;
    private long nombreClientsCollecteurPlus;
    private String nomCollecteurPlusCollectes;
    private BigDecimal montantCollecteurPlus;
    private BigDecimal collecteJournaliere;
    private BigDecimal collecteHebdomadaire;
    private BigDecimal collecteMensuelle;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalClients, long totalCollecteurs, long totalCaissiers, 
                             BigDecimal volumeCotisation, double pourcentageTransactionsValidees,
                             long totalComptesActifs, BigDecimal soldeTotalEpargne,
                             BigDecimal volumeRetraits, long transactionsEnAttente,
                             double tauxPenalites, BigDecimal epargneParClient,
                             String nomCollecteurPlusClients, long nombreClientsCollecteurPlus,
                             String nomCollecteurPlusCollectes, BigDecimal montantCollecteurPlus,
                             BigDecimal collecteJournaliere, BigDecimal collecteHebdomadaire,
                             BigDecimal collecteMensuelle) {
        this.totalClients = totalClients;
        this.totalCollecteurs = totalCollecteurs;
        this.totalCaissiers = totalCaissiers;
        this.volumeCotisation = volumeCotisation;
        this.pourcentageTransactionsValidees = pourcentageTransactionsValidees;
        this.totalComptesActifs = totalComptesActifs;
        this.soldeTotalEpargne = soldeTotalEpargne;
        this.volumeRetraits = volumeRetraits;
        this.transactionsEnAttente = transactionsEnAttente;
        this.tauxPenalites = tauxPenalites;
        this.epargneParClient = epargneParClient;
        this.nomCollecteurPlusClients = nomCollecteurPlusClients;
        this.nombreClientsCollecteurPlus = nombreClientsCollecteurPlus;
        this.nomCollecteurPlusCollectes = nomCollecteurPlusCollectes;
        this.montantCollecteurPlus = montantCollecteurPlus;
        this.collecteJournaliere = collecteJournaliere;
        this.collecteHebdomadaire = collecteHebdomadaire;
        this.collecteMensuelle = collecteMensuelle;
    }

    public long getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(long totalClients) {
        this.totalClients = totalClients;
    }

    public long getTotalCollecteurs() {
        return totalCollecteurs;
    }

    public void setTotalCollecteurs(long totalCollecteurs) {
        this.totalCollecteurs = totalCollecteurs;
    }

    public long getTotalCaissiers() {
        return totalCaissiers;
    }

    public void setTotalCaissiers(long totalCaissiers) {
        this.totalCaissiers = totalCaissiers;
    }

    public BigDecimal getVolumeCotisation() {
        return volumeCotisation;
    }

    public void setVolumeCotisation(BigDecimal volumeCotisation) {
        this.volumeCotisation = volumeCotisation;
    }

    public double getPourcentageTransactionsValidees() {
        return pourcentageTransactionsValidees;
    }

    public void setPourcentageTransactionsValidees(double pourcentageTransactionsValidees) {
        this.pourcentageTransactionsValidees = pourcentageTransactionsValidees;
    }

    public long getTotalComptesActifs() {
        return totalComptesActifs;
    }

    public void setTotalComptesActifs(long totalComptesActifs) {
        this.totalComptesActifs = totalComptesActifs;
    }

    public BigDecimal getSoldeTotalEpargne() {
        return soldeTotalEpargne;
    }

    public void setSoldeTotalEpargne(BigDecimal soldeTotalEpargne) {
        this.soldeTotalEpargne = soldeTotalEpargne;
    }

    public BigDecimal getVolumeRetraits() {
        return volumeRetraits;
    }

    public void setVolumeRetraits(BigDecimal volumeRetraits) {
        this.volumeRetraits = volumeRetraits;
    }

    public long getTransactionsEnAttente() {
        return transactionsEnAttente;
    }

    public void setTransactionsEnAttente(long transactionsEnAttente) {
        this.transactionsEnAttente = transactionsEnAttente;
    }

    public double getTauxPenalites() {
        return tauxPenalites;
    }

    public void setTauxPenalites(double tauxPenalites) {
        this.tauxPenalites = tauxPenalites;
    }

    public BigDecimal getEpargneParClient() {
        return epargneParClient;
    }

    public void setEpargneParClient(BigDecimal epargneParClient) {
        this.epargneParClient = epargneParClient;
    }

    public String getNomCollecteurPlusClients() {
        return nomCollecteurPlusClients;
    }

    public void setNomCollecteurPlusClients(String nomCollecteurPlusClients) {
        this.nomCollecteurPlusClients = nomCollecteurPlusClients;
    }

    public long getNombreClientsCollecteurPlus() {
        return nombreClientsCollecteurPlus;
    }

    public void setNombreClientsCollecteurPlus(long nombreClientsCollecteurPlus) {
        this.nombreClientsCollecteurPlus = nombreClientsCollecteurPlus;
    }

    public String getNomCollecteurPlusCollectes() {
        return nomCollecteurPlusCollectes;
    }

    public void setNomCollecteurPlusCollectes(String nomCollecteurPlusCollectes) {
        this.nomCollecteurPlusCollectes = nomCollecteurPlusCollectes;
    }

    public BigDecimal getMontantCollecteurPlus() {
        return montantCollecteurPlus;
    }

    public void setMontantCollecteurPlus(BigDecimal montantCollecteurPlus) {
        this.montantCollecteurPlus = montantCollecteurPlus;
    }

    public BigDecimal getCollecteJournaliere() {
        return collecteJournaliere;
    }

    public void setCollecteJournaliere(BigDecimal collecteJournaliere) {
        this.collecteJournaliere = collecteJournaliere;
    }

    public BigDecimal getCollecteHebdomadaire() {
        return collecteHebdomadaire;
    }

    public void setCollecteHebdomadaire(BigDecimal collecteHebdomadaire) {
        this.collecteHebdomadaire = collecteHebdomadaire;
    }

    public BigDecimal getCollecteMensuelle() {
        return collecteMensuelle;
    }

    public void setCollecteMensuelle(BigDecimal collecteMensuelle) {
        this.collecteMensuelle = collecteMensuelle;
    }
}
