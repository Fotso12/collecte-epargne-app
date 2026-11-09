package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'inscription d'un collecteur
 * Crée un user + une entrée collector
 */
public class CollectorRegistrationRequest {
    @Size(max = 150)
    @NotNull
    private String fullName;

    @Size(max = 150)
    @NotNull
    private String email;

    @Size(max = 40)
    @NotNull
    private String phone;

    @Size(min = 6, max = 255)
    @NotNull
    private String password;

    @Size(max = 50)
    private String badgeCode; // Code badge du collecteur

    @Size(max = 100)
    private String zone; // Zone d'intervention

    // Institution par défaut (id=1)
    private Long institutionId = 1L;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBadgeCode() {
        return badgeCode;
    }

    public void setBadgeCode(String badgeCode) {
        this.badgeCode = badgeCode;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }
}

