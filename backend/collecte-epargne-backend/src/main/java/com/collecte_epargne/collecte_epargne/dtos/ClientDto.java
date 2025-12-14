package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.TypeCNI;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto implements Serializable {
    @Size(max = 50)
    String codeClient;

    @Size(max = 50)
    @NotNull
    String numeroClient;

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
}