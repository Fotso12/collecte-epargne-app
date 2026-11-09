package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour le Dashboard du Caissier
 * Affiche les KPIs de l'agence du caissier uniquement
 */
public class CaissierDashboardDTO {
    
    // Infos du caissier
    private Integer idCaissier;
    private String nomCaissier;
    private String agenceNom;
    
    // KPIs du jour
    private long transactionsEnAttente;
    private long transactionsValideeAujourdhui;
    private BigDecimal montantValideAujourd;
    private long clientsTotal;
    private long collecteursTotal;
    private BigDecimal gainsJourCaissier;
    
    // Totaux période
    private BigDecimal montantValideTotal;
    private long transactionsTotal;
    
    // Top collecteurs de l'agence
    private List<CollecteurKPIDTO> topCollecteurs;
    
    // Dernières transactions
    private List<TransactionDto> derniereTransactions;

    public CaissierDashboardDTO() {}

    // Getters & Setters
    public Integer getIdCaissier() { return idCaissier; }
    public void setIdCaissier(Integer idCaissier) { this.idCaissier = idCaissier; }

    public String getNomCaissier() { return nomCaissier; }
    public void setNomCaissier(String nomCaissier) { this.nomCaissier = nomCaissier; }

    public String getAgenceNom() { return agenceNom; }
    public void setAgenceNom(String agenceNom) { this.agenceNom = agenceNom; }

    public long getTransactionsEnAttente() { return transactionsEnAttente; }
    public void setTransactionsEnAttente(long transactionsEnAttente) { this.transactionsEnAttente = transactionsEnAttente; }

    public long getTransactionsValideeAujourdhui() { return transactionsValideeAujourdhui; }
    public void setTransactionsValideeAujourdhui(long transactionsValideeAujourdhui) { this.transactionsValideeAujourdhui = transactionsValideeAujourdhui; }

    public BigDecimal getMontantValideAujourd() { return montantValideAujourd; }
    public void setMontantValideAujourd(BigDecimal montantValideAujourd) { this.montantValideAujourd = montantValideAujourd; }

    public long getClientsTotal() { return clientsTotal; }
    public void setClientsTotal(long clientsTotal) { this.clientsTotal = clientsTotal; }

    public long getCollecteursTotal() { return collecteursTotal; }
    public void setCollecteursTotal(long collecteursTotal) { this.collecteursTotal = collecteursTotal; }

    public BigDecimal getGainsJourCaissier() { return gainsJourCaissier; }
    public void setGainsJourCaissier(BigDecimal gainsJourCaissier) { this.gainsJourCaissier = gainsJourCaissier; }

    public BigDecimal getMontantValideTotal() { return montantValideTotal; }
    public void setMontantValideTotal(BigDecimal montantValideTotal) { this.montantValideTotal = montantValideTotal; }

    public long getTransactionsTotal() { return transactionsTotal; }
    public void setTransactionsTotal(long transactionsTotal) { this.transactionsTotal = transactionsTotal; }

    public List<CollecteurKPIDTO> getTopCollecteurs() { return topCollecteurs; }
    public void setTopCollecteurs(List<CollecteurKPIDTO> topCollecteurs) { this.topCollecteurs = topCollecteurs; }

    public List<TransactionDto> getDerniereTransactions() { return derniereTransactions; }
    public void setDerniereTransactions(List<TransactionDto> derniereTransactions) { this.derniereTransactions = derniereTransactions; }
}
