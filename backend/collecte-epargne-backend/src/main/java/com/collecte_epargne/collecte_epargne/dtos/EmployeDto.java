package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

}