package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long totalClients;
    private long totalCollecteurs;
    private long totalCaissiers;
    private BigDecimal volumeCotisation;
    private double pourcentageTransactionsValidees;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalClients, long totalCollecteurs, long totalCaissiers, BigDecimal volumeCotisation, double pourcentageTransactionsValidees) {
        this.totalClients = totalClients;
        this.totalCollecteurs = totalCollecteurs;
        this.totalCaissiers = totalCaissiers;
        this.volumeCotisation = volumeCotisation;
        this.pourcentageTransactionsValidees = pourcentageTransactionsValidees;
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
}
