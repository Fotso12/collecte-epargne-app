package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.ModeTransaction;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;


@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @Size(max = 50)
    @Column(name = "ID_TRANSACTION", nullable = false, length = 50)
    private String idTransaction;

    // Remplacer ID_COMPTE par la relation ManyToOne vers Compte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPTE", nullable = false)
    private Compte compte;

    // Les 3 références à EMPLOYE (Initiateur, Caissier, Superviseur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPLOYE_INITIATEUR")
    private Employe initiateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CAISSIER_VALIDATEUR")
    private Employe caissierValidateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUPERVISEUR_VALIDATEUR")
    private Employe superviseurValidateur;

    // Relation OneToOne vers Recu et Notification (si l'inverse est nécessaire)
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recu recu;

    @OneToMany(mappedBy = "transaction")
    private Set<Notification> notifications;

    @Size(max = 50)
    @NotNull
    @Column(name = "REFERENCE", nullable = false, length = 50)
    private String reference;

    @NotNull
    @Lob
    @Column(name = "TYPE_TRANSACTION", nullable = false)
    private TypeTransaction typeTransaction;

    @NotNull
    @Column(name = "MONTANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @NotNull
    @Column(name = "SOLDE_AVANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeAvant;

    @NotNull
    @Column(name = "SOLDE_APRES", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeApres;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATE_TRANSACTION")
    private Instant dateTransaction;

    @Column(name = "DATE_VALIDATION_CAISSE")
    private Instant dateValidationCaisse;

    @Column(name = "DATE_VALIDATION_SUPERVISEUR")
    private Instant dateValidationSuperviseur;

    @Lob
    @Column(name = "MOTIF_REJET")
    private String motifRejet;

    @Lob
    @Column(name = "STATUT")
    private StatutTransaction statut;

    @Lob
    @Column(name = "MODE_TRANSACTION")
    private ModeTransaction modeTransaction;

    @Lob
    @Column(name = "SIGNATURE_CLIENT")
    private String signatureClient;

    @Size(max = 255)
    @Column(name = "HASH_TRANSACTION")
    private String hashTransaction;

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public Employe getInitiateur() {
        return initiateur;
    }

    public void setInitiateur(Employe initiateur) {
        this.initiateur = initiateur;
    }

    public Employe getCaissierValidateur() {
        return caissierValidateur;
    }

    public void setCaissierValidateur(Employe caissierValidateur) {
        this.caissierValidateur = caissierValidateur;
    }

    public Employe getSuperviseurValidateur() {
        return superviseurValidateur;
    }

    public void setSuperviseurValidateur(Employe superviseurValidateur) {
        this.superviseurValidateur = superviseurValidateur;
    }

    public Recu getRecu() {
        return recu;
    }

    public void setRecu(Recu recu) {
        this.recu = recu;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
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

    public Transaction(String idTransaction, Compte compte, Employe initiateur, Employe caissierValidateur, Employe superviseurValidateur, Recu recu, Set<Notification> notifications, String reference, TypeTransaction typeTransaction, BigDecimal montant, BigDecimal soldeAvant, BigDecimal soldeApres, String description, Instant dateTransaction, Instant dateValidationCaisse, Instant dateValidationSuperviseur, String motifRejet, StatutTransaction statut, ModeTransaction modeTransaction, String signatureClient, String hashTransaction) {
        this.idTransaction = idTransaction;
        this.compte = compte;
        this.initiateur = initiateur;
        this.caissierValidateur = caissierValidateur;
        this.superviseurValidateur = superviseurValidateur;
        this.recu = recu;
        this.notifications = notifications;
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

    public Transaction() {
    }
}