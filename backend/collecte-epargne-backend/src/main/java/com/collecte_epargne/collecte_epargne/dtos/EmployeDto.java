package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour Employe
 */
@Value
public class EmployeDto implements Serializable {
    @Size(max = 50)
    String idEmploye;

    @Size(max = 50)
    @NotNull
    String matricule;

    @NotNull
    LocalDate dateEmbauche;

    @NotNull
    TypeEmploye typeEmploye;

    BigDecimal commissionTaux;

    // Remplacer Utilisateur par le LOGIN
    @NotNull
    String loginUtilisateur;

    // Remplacer AgenceZone par son ID
    String idAgenceZone;

    // Remplacer Superviseur par son ID_EMPLOYE
    String idSuperviseur;

    // Équipe supervisée et clients assignés omis
}