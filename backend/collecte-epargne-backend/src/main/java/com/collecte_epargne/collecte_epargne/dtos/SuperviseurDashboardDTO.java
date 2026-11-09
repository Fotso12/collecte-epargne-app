package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour le Dashboard du Superviseur
 * Affiche les KPIs de l'agence du superviseur uniquement
 */
public class SuperviseurDashboardDTO {
    
    // Infos du superviseur
    private Integer idSuperviseur;
    private String nomSuperviseur;
    private String agenceNom;
    
    // KPIs du jour/semaine
    private long comptesEnAttenteApprobation;
    private long collecteursTotal;
    private BigDecimal montantCollecteJour;
    private BigDecimal gainsJourSuperviseur;
    
    // Meilleur collecteur
    private CollecteurKPIDTO meilleurCollecteur;
    
    // Historique collection
    private List<CollectionKPIDTO> historiquesCollection;

    public SuperviseurDashboardDTO() {}

    // Getters & Setters
    public Integer getIdSuperviseur() { return idSuperviseur; }
    public void setIdSuperviseur(Integer idSuperviseur) { this.idSuperviseur = idSuperviseur; }

    public String getNomSuperviseur() { return nomSuperviseur; }
    public void setNomSuperviseur(String nomSuperviseur) { this.nomSuperviseur = nomSuperviseur; }

    public String getAgenceNom() { return agenceNom; }
    public void setAgenceNom(String agenceNom) { this.agenceNom = agenceNom; }

    public long getComptesEnAttenteApprobation() { return comptesEnAttenteApprobation; }
    public void setComptesEnAttenteApprobation(long comptesEnAttenteApprobation) { this.comptesEnAttenteApprobation = comptesEnAttenteApprobation; }

    public long getCollecteursTotal() { return collecteursTotal; }
    public void setCollecteursTotal(long collecteursTotal) { this.collecteursTotal = collecteursTotal; }

    public BigDecimal getMontantCollecteJour() { return montantCollecteJour; }
    public void setMontantCollecteJour(BigDecimal montantCollecteJour) { this.montantCollecteJour = montantCollecteJour; }

    public BigDecimal getGainsJourSuperviseur() { return gainsJourSuperviseur; }
    public void setGainsJourSuperviseur(BigDecimal gainsJourSuperviseur) { this.gainsJourSuperviseur = gainsJourSuperviseur; }

    public CollecteurKPIDTO getMeilleurCollecteur() { return meilleurCollecteur; }
    public void setMeilleurCollecteur(CollecteurKPIDTO meilleurCollecteur) { this.meilleurCollecteur = meilleurCollecteur; }

    public List<CollectionKPIDTO> getHistoriquesCollection() { return historiquesCollection; }
    public void setHistoriquesCollection(List<CollectionKPIDTO> historiquesCollection) { this.historiquesCollection = historiquesCollection; }
}
