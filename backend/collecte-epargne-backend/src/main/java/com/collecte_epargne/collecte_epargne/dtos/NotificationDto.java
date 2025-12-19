package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.CategorieNotification;
import com.collecte_epargne.collecte_epargne.utils.TypeNotification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;


public class NotificationDto implements Serializable {
    @Size(max = 50)
    String idNotification;

    @Size(max = 50)
    String codeClient;

    // Remplacer Transaction par son ID_TRANSACTION
    String idTransaction;

    @NotNull
    TypeNotification type;

    @NotNull
    CategorieNotification categorie;

    @Size(max = 100)
    String titre;

    @NotNull
    String message;

    String statut;

    Instant dateCreation;

    Instant dateEnvoi;

    Instant dateLecture;

    String erreurEnvoi;

    public NotificationDto(String idNotification, String codeClient, String idTransaction, TypeNotification type, CategorieNotification categorie, String titre, String message, String statut, Instant dateCreation, Instant dateEnvoi, Instant dateLecture, String erreurEnvoi) {
        this.idNotification = idNotification;
        this.codeClient = codeClient;
        this.idTransaction = idTransaction;
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

    public NotificationDto() {
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

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
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
}