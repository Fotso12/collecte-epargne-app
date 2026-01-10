package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour créer une institution/agence
 */
@Getter
@Setter
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
}




