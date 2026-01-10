package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.time.Instant;

@Entity
@Table(name = "agence_zone")
public class AgenceZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AGENCE", nullable = false)
    private Integer idAgence;

    @Size(max = 50)
    @NotNull
    @Column(name = "CODE", nullable = false, length = 50)
    private String code;

    @Size(max = 100)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 100)
    private String nom;

    @NotNull
    @Size(max = 50)
    @Column(name = "VILLE", length = 50)
    private String ville;

    @NotNull
    @Size(max = 50)
    @Column(name = "QUARTIER", length = 50)
    private String quartier;

    @Size(max = 255)
    @Column(name = "ADRESSE")
    private String adresse;

    @NotNull
    @Size(max = 20)
    @Column(name = "TELEPHONE", length = 20)
    private String telephone;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull
    @Lob
    @Column(name = "STATUT")
    private StatutGenerique statut;

    @NotNull
    @Column(name = "DATE_CREATION")
    private Instant dateCreation;

    @Size(max = 255)
    @Column(name = "POSITION")
    private String position;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public AgenceZone(Integer idAgence, String code, String nom, String ville, String quartier, String adresse, String telephone, String description, StatutGenerique statut, Instant dateCreation) {
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

    public AgenceZone() {
    }

    public Throwable getId() {
        return null;
    }
}