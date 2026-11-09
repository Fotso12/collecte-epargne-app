package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour créer un utilisateur (caissier, collecteur, superviseur, auditeur)
 * Par un admin
 */
public class CreateUserRequest {
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

    @NotNull
    private String roleCode; // "caissier", "collector", "supervisor", "auditor"

    @NotNull
    private Long institutionId;

    // Champs spécifiques pour collecteur
    @Size(max = 50)
    private String badgeCode; // Code badge du collecteur

    @Size(max = 100)
    private String zone; // Zone d'intervention

    // Champs spécifiques pour caissier
    @Size(max = 50)
    private String matricule; // Matricule du caissier

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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
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

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
}




