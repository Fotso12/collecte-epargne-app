package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutDemande;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


public class DemandeOuvertureCompteDto implements Serializable {
    Long idDemande;

    @NotNull
    String codeClient;

    @NotNull
    Integer idTypeCompte;

    String nomTypeCompte; // Pour l'affichage

    Integer idSuperviseurValidateur;

    @NotNull
    StatutDemande statut;

    BigDecimal montantInitial;

    String motif;

    String motifRejet;

    Instant dateDemande;

    Instant dateValidation;

    // Informations du client (pour l'affichage)
    String nomClient;
    String prenomClient;
    String emailClient;

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
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

    public String getNomTypeCompte() {
        return nomTypeCompte;
    }

    public void setNomTypeCompte(String nomTypeCompte) {
        this.nomTypeCompte = nomTypeCompte;
    }

    public Integer getIdSuperviseurValidateur() {
        return idSuperviseurValidateur;
    }

    public void setIdSuperviseurValidateur(Integer idSuperviseurValidateur) {
        this.idSuperviseurValidateur = idSuperviseurValidateur;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public BigDecimal getMontantInitial() {
        return montantInitial;
    }

    public void setMontantInitial(BigDecimal montantInitial) {
        this.montantInitial = montantInitial;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public Instant getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Instant dateDemande) {
        this.dateDemande = dateDemande;
    }

    public Instant getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(Instant dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenomClient() {
        return prenomClient;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public DemandeOuvertureCompteDto(Long idDemande, String codeClient, Integer idTypeCompte, String nomTypeCompte, Integer idSuperviseurValidateur, StatutDemande statut, BigDecimal montantInitial, String motif, String motifRejet, Instant dateDemande, Instant dateValidation, String nomClient, String prenomClient, String emailClient) {
        this.idDemande = idDemande;
        this.codeClient = codeClient;
        this.idTypeCompte = idTypeCompte;
        this.nomTypeCompte = nomTypeCompte;
        this.idSuperviseurValidateur = idSuperviseurValidateur;
        this.statut = statut;
        this.montantInitial = montantInitial;
        this.motif = motif;
        this.motifRejet = motifRejet;
        this.dateDemande = dateDemande;
        this.dateValidation = dateValidation;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.emailClient = emailClient;
    }

    public DemandeOuvertureCompteDto() {
    }
}

