package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


@Entity
@Table(name = "type_compte")
public class TypeCompte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TYPE", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "CODE", nullable = false, length = 20)
    private String code;

    @Size(max = 50)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 50)
    private String nom;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TAUX_INTERET", precision = 5, scale = 2)
    private BigDecimal tauxInteret;

    @Column(name = "SOLDE_MINIMUM", precision = 15, scale = 2)
    private BigDecimal soldeMinimum;

    @Column(name = "FRAIS_OUVERTURE", precision = 15, scale = 2)
    private BigDecimal fraisOuverture;

    @Column(name = "FRAIS_CLOTURE", precision = 15, scale = 2)
    private BigDecimal fraisCloture;

    @Column(name = "AUTORISER_RETRAIT")
    private Boolean autoriserRetrait;

    @Column(name = "DUREE_BLOCAGE_JOURS")
    private Integer dureeBlocageJours;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(BigDecimal tauxInteret) {
        this.tauxInteret = tauxInteret;
    }

    public BigDecimal getSoldeMinimum() {
        return soldeMinimum;
    }

    public void setSoldeMinimum(BigDecimal soldeMinimum) {
        this.soldeMinimum = soldeMinimum;
    }

    public BigDecimal getFraisOuverture() {
        return fraisOuverture;
    }

    public void setFraisOuverture(BigDecimal fraisOuverture) {
        this.fraisOuverture = fraisOuverture;
    }

    public BigDecimal getFraisCloture() {
        return fraisCloture;
    }

    public void setFraisCloture(BigDecimal fraisCloture) {
        this.fraisCloture = fraisCloture;
    }

    public Boolean getAutoriserRetrait() {
        return autoriserRetrait;
    }

    public void setAutoriserRetrait(Boolean autoriserRetrait) {
        this.autoriserRetrait = autoriserRetrait;
    }

    public Integer getDureeBlocageJours() {
        return dureeBlocageJours;
    }

    public void setDureeBlocageJours(Integer dureeBlocageJours) {
        this.dureeBlocageJours = dureeBlocageJours;
    }

    public TypeCompte(Integer id, String code, String nom, String description, BigDecimal tauxInteret, BigDecimal soldeMinimum, BigDecimal fraisOuverture, BigDecimal fraisCloture, Boolean autoriserRetrait, Integer dureeBlocageJours) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.tauxInteret = tauxInteret;
        this.soldeMinimum = soldeMinimum;
        this.fraisOuverture = fraisOuverture;
        this.fraisCloture = fraisCloture;
        this.autoriserRetrait = autoriserRetrait;
        this.dureeBlocageJours = dureeBlocageJours;
    }
}