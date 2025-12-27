package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.TypeCNI;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.io.Serializable;
import java.time.LocalDate;


public class ClientDto implements Serializable {
    @Size(max = 50)
    String codeClient;


    @NotNull
    Long numeroClient;

    @Size(max = 255)
    String adresse;

    @NotNull
    TypeCNI typeCni;

    @Size(max = 50)
    @NotNull
    String numCni;

    @Size(max = 255)
    String photoPath;

    @Size(max = 255)
    String cniRectoPath;

    @Size(max = 255)
    String cniVersoPath;

    LocalDate dateNaissance;

    @Size(max = 100)
    String lieuNaissance;

    @Size(max = 100)
    String profession;

    Integer scoreEpargne;

    // Remplacer Utilisateur par le LOGIN
    @NotNull
    String loginUtilisateur;

    // Remplacer Employe par l'ID du Collecteur
    String codeCollecteurAssigne;

    // Les comptes sont généralement omis ou chargés séparément


    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public Long getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(Long numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public TypeCNI getTypeCni() {
        return typeCni;
    }

    public void setTypeCni(TypeCNI typeCni) {
        this.typeCni = typeCni;
    }

    public String getNumCni() {
        return numCni;
    }

    public void setNumCni(String numCni) {
        this.numCni = numCni;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCniRectoPath() {
        return cniRectoPath;
    }

    public void setCniRectoPath(String cniRectoPath) {
        this.cniRectoPath = cniRectoPath;
    }

    public String getCniVersoPath() {
        return cniVersoPath;
    }

    public void setCniVersoPath(String cniVersoPath) {
        this.cniVersoPath = cniVersoPath;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Integer getScoreEpargne() {
        return scoreEpargne;
    }

    public void setScoreEpargne(Integer scoreEpargne) {
        this.scoreEpargne = scoreEpargne;
    }

    public String getLoginUtilisateur() {
        return loginUtilisateur;
    }

    public void setLoginUtilisateur(String loginUtilisateur) {
        this.loginUtilisateur = loginUtilisateur;
    }

    public String getCodeCollecteurAssigne() {
        return codeCollecteurAssigne;
    }

    public void setCodeCollecteurAssigne(String codeCollecteurAssigne) {
        this.codeCollecteurAssigne = codeCollecteurAssigne;
    }

    public ClientDto() {
    }

    public ClientDto(String codeClient, Long numeroClient, String adresse, TypeCNI typeCni, String numCni, String photoPath, String cniRectoPath, String cniVersoPath, LocalDate dateNaissance, String lieuNaissance, String profession, Integer scoreEpargne, String loginUtilisateur, String codeCollecteurAssigne) {
        this.codeClient = codeClient;
        this.numeroClient = numeroClient;
        this.adresse = adresse;
        this.typeCni = typeCni;
        this.numCni = numCni;
        this.photoPath = photoPath;
        this.cniRectoPath = cniRectoPath;
        this.cniVersoPath = cniVersoPath;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;
        this.profession = profession;
        this.scoreEpargne = scoreEpargne;
        this.loginUtilisateur = loginUtilisateur;
        this.codeCollecteurAssigne = codeCollecteurAssigne;
    }
}