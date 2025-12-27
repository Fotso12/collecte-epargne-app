package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;


public class TypeCompteDto implements Serializable {
    Integer id;

    @Size(max = 20)
    @NotNull
    String code;

    @Size(max = 50)
    @NotNull
    String nom;

    String description;

    BigDecimal tauxInteret;

    BigDecimal soldeMinimum;

    BigDecimal fraisOuverture;

    BigDecimal fraisCloture;

    Boolean autoriserRetrait;

    Integer dureeBlocageJours;

    public TypeCompteDto(Integer id, String code, String nom, String description, BigDecimal tauxInteret, BigDecimal soldeMinimum, BigDecimal fraisOuverture, BigDecimal fraisCloture, Boolean autoriserRetrait, Integer dureeBlocageJours) {
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

    public TypeCompteDto() {
    }

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
}