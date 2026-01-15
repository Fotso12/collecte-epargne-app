package com.collecte_epargne.collecte_epargne.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'inscription d'un client
 * Les clients ne sont PAS des users - table séparée
 */
public class ClientRegistrationRequest {
    @Size(max = 150)
    @NotNull
    private String fullName;

    @Size(max = 40)
    @NotNull
    private String phone;

    @Size(max = 40)
    private String identityType; // CNI, Passport, etc.

    @Size(max = 80)
    private String identityNumber;

    private String address;

    private String ville;

    // Matricule du collecteur parrain (optionnel)
    // Si "0000" ou vide, le client ne sera pas affilié à un collecteur
    @Size(max = 50)
    private String collectorMatricule;

    // Email pour la connexion
    @Size(max = 100)
    @NotNull
    private String email;

    // Mot de passe pour la connexion
    @Size(min = 6, max = 255)
    @NotNull
    private String password;

    // Institution par défaut (id=1)
    private Long institutionId = 1L;

    // Nouveaux champs pour KYC
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate dateNaissance;

    @Size(max = 100)
    @NotNull
    private String lieuNaissance;

    @Size(max = 100)
    @NotNull
    private String profession;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCollectorMatricule() {
        return collectorMatricule;
    }

    public void setCollectorMatricule(String collectorMatricule) {
        this.collectorMatricule = collectorMatricule;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public java.time.LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(java.time.LocalDate dateNaissance) {
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
}

