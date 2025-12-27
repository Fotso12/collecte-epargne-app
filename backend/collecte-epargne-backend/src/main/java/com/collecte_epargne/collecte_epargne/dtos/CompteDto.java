package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutCompte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


public class CompteDto implements Serializable {
    @Size(max = 50)
    String idCompte;

    @Size(max = 50)
    @NotNull
    String numCompte;

    BigDecimal solde;

    BigDecimal soldeDisponible;

    @NotNull
    LocalDate dateOuverture;

    Instant dateDerniereTransaction;

    BigDecimal tauxPenalite;

    BigDecimal tauxBonus;

    StatutCompte statut;

    String motifBlocage;

    LocalDate dateCloture;

    // Remplacer Client par son CODE_CLIENT
    @NotNull
    String codeClient;

    // Remplacer TypeCompte par son ID
    @NotNull
    Integer idTypeCompte;

    // Transactions et plans de cotisations omis


    public CompteDto(String idCompte, String numCompte, BigDecimal solde, BigDecimal soldeDisponible, LocalDate dateOuverture, Instant dateDerniereTransaction, BigDecimal tauxPenalite, BigDecimal tauxBonus, StatutCompte statut, String motifBlocage, LocalDate dateCloture, String codeClient, Integer idTypeCompte) {
        this.idCompte = idCompte;
        this.numCompte = numCompte;
        this.solde = solde;
        this.soldeDisponible = soldeDisponible;
        this.dateOuverture = dateOuverture;
        this.dateDerniereTransaction = dateDerniereTransaction;
        this.tauxPenalite = tauxPenalite;
        this.tauxBonus = tauxBonus;
        this.statut = statut;
        this.motifBlocage = motifBlocage;
        this.dateCloture = dateCloture;
        this.codeClient = codeClient;
        this.idTypeCompte = idTypeCompte;
    }

    public CompteDto() {
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public String getNumCompte() {
        return numCompte;
    }

    public void setNumCompte(String numCompte) {
        this.numCompte = numCompte;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public BigDecimal getSoldeDisponible() {
        return soldeDisponible;
    }

    public void setSoldeDisponible(BigDecimal soldeDisponible) {
        this.soldeDisponible = soldeDisponible;
    }

    public LocalDate getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDate dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Instant getDateDerniereTransaction() {
        return dateDerniereTransaction;
    }

    public void setDateDerniereTransaction(Instant dateDerniereTransaction) {
        this.dateDerniereTransaction = dateDerniereTransaction;
    }

    public BigDecimal getTauxPenalite() {
        return tauxPenalite;
    }

    public void setTauxPenalite(BigDecimal tauxPenalite) {
        this.tauxPenalite = tauxPenalite;
    }

    public BigDecimal getTauxBonus() {
        return tauxBonus;
    }

    public void setTauxBonus(BigDecimal tauxBonus) {
        this.tauxBonus = tauxBonus;
    }

    public StatutCompte getStatut() {
        return statut;
    }

    public void setStatut(StatutCompte statut) {
        this.statut = statut;
    }

    public String getMotifBlocage() {
        return motifBlocage;
    }

    public void setMotifBlocage(String motifBlocage) {
        this.motifBlocage = motifBlocage;
    }

    public LocalDate getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDate dateCloture) {
        this.dateCloture = dateCloture;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public Integer getIdTypeCompte() {
        return idTypeCompte;
    }

    public void setIdTypeCompte(Integer idTypeCompte) {
        this.idTypeCompte = idTypeCompte;
    }
}