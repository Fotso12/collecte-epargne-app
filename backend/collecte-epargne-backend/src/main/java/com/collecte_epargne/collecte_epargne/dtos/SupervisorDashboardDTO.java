package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;

/**
 * COMMENT CLEF: Dashboard KPIs pour superviseur
 * Vue globale de l'institution
 */
public class SupervisorDashboardDTO {
    private BigDecimal totalCollectedToday;
    private Long pendingAccountApprovals;
    private BigDecimal totalCollectedThisMonth;
    private String topCollectorName;
    private Integer totalEmployees;

    public SupervisorDashboardDTO(BigDecimal totalCollectedToday, Long pendingAccountApprovals,
                                  BigDecimal totalCollectedThisMonth, String topCollectorName,
                                  Integer totalEmployees) {
        this.totalCollectedToday = totalCollectedToday;
        this.pendingAccountApprovals = pendingAccountApprovals;
        this.totalCollectedThisMonth = totalCollectedThisMonth;
        this.topCollectorName = topCollectorName;
        this.totalEmployees = totalEmployees;
    }

    public BigDecimal getTotalCollectedToday() { return totalCollectedToday; }
    public Long getPendingAccountApprovals() { return pendingAccountApprovals; }
    public BigDecimal getTotalCollectedThisMonth() { return totalCollectedThisMonth; }
    public String getTopCollectorName() { return topCollectorName; }
    public Integer getTotalEmployees() { return totalEmployees; }
}
