package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * COMMENT CLEF: KPIs de collection par période
 * Utilisé pour les rapports et analyses superviseur
 */
public class CollectionKPIDTO {
    private BigDecimal totalCollected;
    private String topCollector;
    private Long activeClients;
    private LocalDate startDate;
    private LocalDate endDate;

    public CollectionKPIDTO(BigDecimal totalCollected, String topCollector, Long activeClients,
                           LocalDate startDate, LocalDate endDate) {
        this.totalCollected = totalCollected;
        this.topCollector = topCollector;
        this.activeClients = activeClients;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public BigDecimal getTotalCollected() { return totalCollected; }
    public String getTopCollector() { return topCollector; }
    public Long getActiveClients() { return activeClients; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
