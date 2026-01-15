package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


public class TransactionOfflineDto implements Serializable {
    @Size(max = 50)
    String idOffline;

    BigDecimal montant;

    TypeTransaction typeTransaction;

    @NotNull
    String dateTransaction;

    String description;

    String signatureClient;

    BigDecimal latitude;

    BigDecimal longitude;

    StatutSynchroOffline statutSynchro;

    Instant dateSynchro;

    String erreurSynchro;

    // Remplacer Employe par son ID_EMPLOYE
    String idEmploye;

    // Remplacer Client par son CODE_CLIENT
    String codeClient;

    // Remplacer Compte par son ID_COMPTE
    String idCompte;

    // Remplacer Transaction par son ID_TRANSACTION
    String idTransactionFinale;

    String idCaissierValidation;

    public TransactionOfflineDto(String idOffline, BigDecimal montant, TypeTransaction typeTransaction, String dateTransaction, String description, String signatureClient, BigDecimal latitude, BigDecimal longitude, StatutSynchroOffline statutSynchro, Instant dateSynchro, String erreurSynchro, String idEmploye, String codeClient, String idCompte, String idTransactionFinale) {
        this.idOffline = idOffline;
        this.montant = montant;
        this.typeTransaction = typeTransaction;
        this.dateTransaction = dateTransaction;
        this.description = description;
        this.signatureClient = signatureClient;
        this.latitude = latitude;
        this.longitude = longitude;
        this.statutSynchro = statutSynchro;
        this.dateSynchro = dateSynchro;
        this.erreurSynchro = erreurSynchro;
        this.idEmploye = idEmploye;
        this.codeClient = codeClient;
        this.idCompte = idCompte;
        this.idTransactionFinale = idTransactionFinale;
    }

    public TransactionOfflineDto() {
    }

    public String getIdOffline() {
        return idOffline;
    }

    public void setIdOffline(String idOffline) {
        this.idOffline = idOffline;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public String getDateTransaction() {
        return dateTransaction;
    }
 
    public void setDateTransaction(String dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignatureClient() {
        return signatureClient;
    }

    public void setSignatureClient(String signatureClient) {
        this.signatureClient = signatureClient;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public StatutSynchroOffline getStatutSynchro() {
        return statutSynchro;
    }

    public void setStatutSynchro(StatutSynchroOffline statutSynchro) {
        this.statutSynchro = statutSynchro;
    }

    public Instant getDateSynchro() {
        return dateSynchro;
    }

    public void setDateSynchro(Instant dateSynchro) {
        this.dateSynchro = dateSynchro;
    }

    public String getErreurSynchro() {
        return erreurSynchro;
    }

    public void setErreurSynchro(String erreurSynchro) {
        this.erreurSynchro = erreurSynchro;
    }

    public String getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(String idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public String getIdTransactionFinale() {
        return idTransactionFinale;
    }

    public void setIdTransactionFinale(String idTransactionFinale) {
        this.idTransactionFinale = idTransactionFinale;
    }

    public String getIdCaissierValidation() {
        return idCaissierValidation;
    }

    public void setIdCaissierValidation(String idCaissierValidation) {
        this.idCaissierValidation = idCaissierValidation;
    }
}