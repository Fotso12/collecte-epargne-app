package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;

/**
 * COMMENT CLEF: Dashboard KPIs pour caissier
 * Affiche les statistiques du jour/agence
 */
public class CashierDashboardDTO {
    private BigDecimal totalCollectedToday;
    private Long pendingTransactionCount;
    private Integer activeCollectors;
    private Integer activeClients;

    public CashierDashboardDTO(BigDecimal totalCollectedToday, Long pendingTransactionCount,
                               Integer activeCollectors, Integer activeClients) {
        this.totalCollectedToday = totalCollectedToday;
        this.pendingTransactionCount = pendingTransactionCount;
        this.activeCollectors = activeCollectors;
        this.activeClients = activeClients;
    }

    public BigDecimal getTotalCollectedToday() { return totalCollectedToday; }
    public Long getPendingTransactionCount() { return pendingTransactionCount; }
    public Integer getActiveCollectors() { return activeCollectors; }
    public Integer getActiveClients() { return activeClients; }
}
