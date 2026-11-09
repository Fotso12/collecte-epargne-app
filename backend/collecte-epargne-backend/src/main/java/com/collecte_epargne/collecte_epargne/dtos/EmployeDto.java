package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


public class EmployeDto implements Serializable {

    Integer idEmploye;

    @Size(max = 50)
    @NotNull
    String matricule;

    @NotNull
    LocalDate dateEmbauche;

    @NotNull
    private TypeEmploye typeEmploye;

    BigDecimal commissionTaux;

    // Remplacer Utilisateur par le LOGIN
    @NotNull
    String loginUtilisateur;

    // Remplacer AgenceZone par son ID
    private Integer idAgence;

    // Remplacer Superviseur par son ID_EMPLOYE
    String idSuperviseur;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public EmployeDto(Integer idEmploye, String matricule, LocalDate dateEmbauche, TypeEmploye typeEmploye, BigDecimal commissionTaux, String loginUtilisateur, Integer idAgence, String idSuperviseur) {
        this.idEmploye = idEmploye;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.typeEmploye = typeEmploye;
        this.commissionTaux = commissionTaux;
        this.loginUtilisateur = loginUtilisateur;
        this.idAgence = idAgence;
        this.idSuperviseur = idSuperviseur;
    }

    public Integer getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }

    public EmployeDto() {
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public TypeEmploye getTypeEmploye() {
        return typeEmploye;
    }

    public void setTypeEmploye(TypeEmploye typeEmploye) {
        this.typeEmploye = typeEmploye;
    }

    public BigDecimal getCommissionTaux() {
        return commissionTaux;
    }

    public void setCommissionTaux(BigDecimal commissionTaux) {
        this.commissionTaux = commissionTaux;
    }

    public String getLoginUtilisateur() {
        return loginUtilisateur;
    }

    public void setLoginUtilisateur(String loginUtilisateur) {
        this.loginUtilisateur = loginUtilisateur;
    }

    public Integer getIdAgence() {
        return idAgence;
    }

    public void setIdAgence(Integer idAgence) {
        this.idAgence = idAgence;
    }

    public String getIdSuperviseur() {
        return idSuperviseur;
    }

    public void setIdSuperviseur(String idSuperviseur) {
        this.idSuperviseur = idSuperviseur;
    }
}