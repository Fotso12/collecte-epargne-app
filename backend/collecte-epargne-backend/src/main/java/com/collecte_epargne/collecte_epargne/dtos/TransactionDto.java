package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.ModeTransaction;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


public class TransactionDto implements Serializable {
    @Size(max = 50)
    String idTransaction;

    // Remplacer Compte par son ID_COMPTE
    @NotNull
    String idCompte;

    // Remplacer les 3 Employes par leurs IDs
    String idEmployeInitiateur;
    String idCaissierValidateur;
    String idSuperviseurValidateur;

    // Le reçu (Recu) est souvent chargé séparément si nécessaire

    @Size(max = 50)
    @NotNull
    String reference;

    @NotNull
    TypeTransaction typeTransaction;

    @NotNull
    BigDecimal montant;

    @NotNull
    BigDecimal soldeAvant;

    @NotNull
    BigDecimal soldeApres;

    String description;

    Instant dateTransaction;

    Instant dateValidationCaisse;

    Instant dateValidationSuperviseur;

    String motifRejet;

    StatutTransaction statut;

    ModeTransaction modeTransaction;

    String signatureClient;

    @Size(max = 255)
    String hashTransaction;

    // Notifications omises


    public TransactionDto(String idTransaction, String idCompte, String idEmployeInitiateur, String idCaissierValidateur, String idSuperviseurValidateur, String reference, TypeTransaction typeTransaction, BigDecimal montant, BigDecimal soldeAvant, BigDecimal soldeApres, String description, Instant dateTransaction, Instant dateValidationCaisse, Instant dateValidationSuperviseur, String motifRejet, StatutTransaction statut, ModeTransaction modeTransaction, String signatureClient, String hashTransaction) {
        this.idTransaction = idTransaction;
        this.idCompte = idCompte;
        this.idEmployeInitiateur = idEmployeInitiateur;
        this.idCaissierValidateur = idCaissierValidateur;
        this.idSuperviseurValidateur = idSuperviseurValidateur;
        this.reference = reference;
        this.typeTransaction = typeTransaction;
        this.montant = montant;
        this.soldeAvant = soldeAvant;
        this.soldeApres = soldeApres;
        this.description = description;
        this.dateTransaction = dateTransaction;
        this.dateValidationCaisse = dateValidationCaisse;
        this.dateValidationSuperviseur = dateValidationSuperviseur;
        this.motifRejet = motifRejet;
        this.statut = statut;
        this.modeTransaction = modeTransaction;
        this.signatureClient = signatureClient;
        this.hashTransaction = hashTransaction;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public String getIdEmployeInitiateur() {
        return idEmployeInitiateur;
    }

    public void setIdEmployeInitiateur(String idEmployeInitiateur) {
        this.idEmployeInitiateur = idEmployeInitiateur;
    }

    public TransactionDto() {
    }

    public String getIdCaissierValidateur() {
        return idCaissierValidateur;
    }

    public void setIdCaissierValidateur(String idCaissierValidateur) {
        this.idCaissierValidateur = idCaissierValidateur;
    }

    public String getIdSuperviseurValidateur() {
        return idSuperviseurValidateur;
    }

    public void setIdSuperviseurValidateur(String idSuperviseurValidateur) {
        this.idSuperviseurValidateur = idSuperviseurValidateur;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public BigDecimal getSoldeAvant() {
        return soldeAvant;
    }

    public void setSoldeAvant(BigDecimal soldeAvant) {
        this.soldeAvant = soldeAvant;
    }

    public BigDecimal getSoldeApres() {
        return soldeApres;
    }

    public void setSoldeApres(BigDecimal soldeApres) {
        this.soldeApres = soldeApres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Instant dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public Instant getDateValidationCaisse() {
        return dateValidationCaisse;
    }

    public void setDateValidationCaisse(Instant dateValidationCaisse) {
        this.dateValidationCaisse = dateValidationCaisse;
    }

    public Instant getDateValidationSuperviseur() {
        return dateValidationSuperviseur;
    }

    public void setDateValidationSuperviseur(Instant dateValidationSuperviseur) {
        this.dateValidationSuperviseur = dateValidationSuperviseur;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public StatutTransaction getStatut() {
        return statut;
    }

    public void setStatut(StatutTransaction statut) {
        this.statut = statut;
    }

    public ModeTransaction getModeTransaction() {
        return modeTransaction;
    }

    public void setModeTransaction(ModeTransaction modeTransaction) {
        this.modeTransaction = modeTransaction;
    }

    public String getSignatureClient() {
        return signatureClient;
    }

    public void setSignatureClient(String signatureClient) {
        this.signatureClient = signatureClient;
    }

    public String getHashTransaction() {
        return hashTransaction;
    }

    public void setHashTransaction(String hashTransaction) {
        this.hashTransaction = hashTransaction;
    }
}