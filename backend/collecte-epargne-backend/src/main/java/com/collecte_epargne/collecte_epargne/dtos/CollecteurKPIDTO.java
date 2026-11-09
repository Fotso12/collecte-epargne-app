package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;
import java.io.Serializable;

/**
 * DTO pour les KPIs des collecteurs
 * Utilisé par SuperviseurService et CaissierService
 */
public class CollecteurKPIDTO implements Serializable {
    
    private Integer idCollecteur;
    private String nomCollecteur;
    private BigDecimal montantCollecte;
    private long nombreClients;
    private long nombreTransactions;
    private BigDecimal gainsMoyens;

    public CollecteurKPIDTO() {}

    public CollecteurKPIDTO(
            Integer idCollecteur,
            String nomCollecteur,
            BigDecimal montantCollecte,
            long nombreClients,
            long nombreTransactions,
            BigDecimal gainsMoyens) {
        this.idCollecteur = idCollecteur;
        this.nomCollecteur = nomCollecteur;
        this.montantCollecte = montantCollecte;
        this.nombreClients = nombreClients;
        this.nombreTransactions = nombreTransactions;
        this.gainsMoyens = gainsMoyens;
    }

    // Getters & Setters
    public Integer getIdCollecteur() { return idCollecteur; }
    public void setIdCollecteur(Integer idCollecteur) { this.idCollecteur = idCollecteur; }

    public String getNomCollecteur() { return nomCollecteur; }
    public void setNomCollecteur(String nomCollecteur) { this.nomCollecteur = nomCollecteur; }

    public BigDecimal getMontantCollecte() { return montantCollecte; }
    public void setMontantCollecte(BigDecimal montantCollecte) { this.montantCollecte = montantCollecte; }

    public long getNombreClients() { return nombreClients; }
    public void setNombreClients(long nombreClients) { this.nombreClients = nombreClients; }

    public long getNombreTransactions() { return nombreTransactions; }
    public void setNombreTransactions(long nombreTransactions) { this.nombreTransactions = nombreTransactions; }

    public BigDecimal getGainsMoyens() { return gainsMoyens; }
    public void setGainsMoyens(BigDecimal gainsMoyens) { this.gainsMoyens = gainsMoyens; }
}
