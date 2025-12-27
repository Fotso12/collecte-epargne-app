package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name = "transaction_offline")
public class TransactionOffline {
    @Id
    @Size(max = 50)
    @Column(name = "ID_OFFLINE", nullable = false, length = 50)
    private String idOffline;

    @NotNull
    @Column(name = "MONTANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @NotNull
    @Lob
    @Column(name = "TYPE_TRANSACTION", nullable = false)
    private TypeTransaction typeTransaction;

    @NotNull
    @Column(name = "DATE_TRANSACTION", nullable = false)
    private Instant dateTransaction;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "SIGNATURE_CLIENT")
    private String signatureClient;

    @Column(name = "LATITUDE", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Lob
    @Column(name = "STATUT_SYNCHRO")
    private StatutSynchroOffline statutSynchro;

    @Column(name = "DATE_SYNCHRO")
    private Instant dateSynchro;

    @Lob
    @Column(name = "ERREUR_SYNCHRO")
    private String erreurSynchro;

    // Remplacer ID_EMPLOYE par la relation ManyToOne vers Employe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPLOYE", nullable = false)
    private Employe employe;

    // Remplacer CODE_CLIENT (même si on a déjà l'ID_COMPTE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_CLIENT", nullable = false)
    private Client client;

    // Remplacer ID_COMPTE par la relation ManyToOne vers Compte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPTE", nullable = false)
    private Compte compte;

    // Remplacer ID_TRANSACTION_FINALE par la relation ManyToOne vers Transaction
    @OneToOne(fetch = FetchType.LAZY) // Une transaction offline est complétée par une seule Transaction finale
    @JoinColumn(name = "ID_TRANSACTION_FINALE")
    private Transaction transactionFinale;

    public TransactionOffline() {

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

    public Instant getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Instant dateTransaction) {
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

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public Transaction getTransactionFinale() {
        return transactionFinale;
    }

    public void setTransactionFinale(Transaction transactionFinale) {
        this.transactionFinale = transactionFinale;
    }

    public TransactionOffline(String idOffline, BigDecimal montant, TypeTransaction typeTransaction, Instant dateTransaction, String description, String signatureClient, BigDecimal latitude, BigDecimal longitude, StatutSynchroOffline statutSynchro, Instant dateSynchro, String erreurSynchro, Employe employe, Client client, Compte compte, Transaction transactionFinale) {
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
        this.employe = employe;
        this.client = client;
        this.compte = compte;
        this.transactionFinale = transactionFinale;
    }

}