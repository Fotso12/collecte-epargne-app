package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour créer une institution/agence
 */
public class CreateInstitutionRequest {
    @Size(max = 150)
    @NotNull
    private String name;

    @Size(max = 30)
    @NotNull
    private String code;

    @Size(max = 120)
    private String contactEmail;

    @Size(max = 40)
    private String contactPhone;

    @Size(max = 64)
    private String timezone = "Africa/Abidjan";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}




