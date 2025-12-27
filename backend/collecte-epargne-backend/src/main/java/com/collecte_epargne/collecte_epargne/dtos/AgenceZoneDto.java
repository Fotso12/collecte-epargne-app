package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;



public class AgenceZoneDto implements Serializable {
    Integer idAgence;

    @Size(max = 50)
    @NotNull
    String code;

    @Size(max = 100)
    @NotNull
    String nom;

    @Size(max = 50)
    String ville;

    @Size(max = 50)
    String quartier;

    @Size(max = 255)
    String adresse;

    @Size(max = 20)
    String telephone;

    String description;

    StatutGenerique statut;

    Instant dateCreation;

    public Integer getIdAgence() {
        return idAgence;
    }

    public void setIdAgence(Integer idAgence) {
        this.idAgence = idAgence;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutGenerique getStatut() {
        return statut;
    }

    public void setStatut(StatutGenerique statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public AgenceZoneDto() {
    }

    public AgenceZoneDto(Integer idAgence, String code, String nom, String ville, String quartier, String adresse, String telephone, String description, StatutGenerique statut, Instant dateCreation) {
        this.idAgence = idAgence;
        this.code = code;
        this.nom = nom;
        this.ville = ville;
        this.quartier = quartier;
        this.adresse = adresse;
        this.telephone = telephone;
        this.description = description;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }
}