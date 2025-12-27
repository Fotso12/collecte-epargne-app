package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.CategorieNotification;
import com.collecte_epargne.collecte_epargne.utils.TypeNotification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;


@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @Size(max = 50)
    @Column(name = "ID_NOTIFICATION", nullable = false, length = 50)
    private String idNotification;

    @Size(max = 50)
    @Column(name = "CODE_CLIENT", length = 50)
    private String codeClient;

    // Par la relation ManyToOne (doit être nommé "transaction" pour correspondre au mappedBy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TRANSACTION") // ID_TRANSACTION est la colonne FK dans la table NOTIFICATION
    private Transaction transaction;

    @NotNull
    @Lob
    @Column(name = "TYPE", nullable = false)
    private TypeNotification type;

    @NotNull
    @Lob
    @Column(name = "CATEGORIE", nullable = false)
    private CategorieNotification categorie;

    @Size(max = 100)
    @Column(name = "TITRE", length = 100)
    private String titre;

    @NotNull
    @Lob
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @NotNull
    @Size(max = 20) // Valide la longueur en Java
    @Column(name = "STATUT", length = 20) // Fixe la taille de la colonne à VARCHAR(20) dans la DB
    private String statut;

    @Column(name = "DATE_CREATION")
    private Instant dateCreation;

    @Column(name = "DATE_ENVOI")
    private Instant dateEnvoi;

    @Column(name = "DATE_LECTURE")
    private Instant dateLecture;

    @Lob
    @Column(name = "ERREUR_ENVOI")
    private String erreurEnvoi;

    public Notification() {
    }

    public Notification(String idNotification, String codeClient, Transaction transaction, TypeNotification type, CategorieNotification categorie, String titre, String message, String statut, Instant dateCreation, Instant dateEnvoi, Instant dateLecture, String erreurEnvoi) {
        this.idNotification = idNotification;
        this.codeClient = codeClient;
        this.transaction = transaction;
        this.type = type;
        this.categorie = categorie;
        this.titre = titre;
        this.message = message;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateEnvoi = dateEnvoi;
        this.dateLecture = dateLecture;
        this.erreurEnvoi = erreurEnvoi;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public TypeNotification getType() {
        return type;
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }

    public CategorieNotification getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieNotification categorie) {
        this.categorie = categorie;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Instant dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Instant getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(Instant dateLecture) {
        this.dateLecture = dateLecture;
    }

    public String getErreurEnvoi() {
        return erreurEnvoi;
    }

    public void setErreurEnvoi(String erreurEnvoi) {
        this.erreurEnvoi = erreurEnvoi;
    }

    public Notification() {
    }
}