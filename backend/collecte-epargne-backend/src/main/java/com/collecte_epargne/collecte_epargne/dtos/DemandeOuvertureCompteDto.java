package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutDemande;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DemandeOuvertureCompteDto implements Serializable {
    Long idDemande;

    @NotNull
    String codeClient;

    @NotNull
    Integer idTypeCompte;

    String nomTypeCompte; // Pour l'affichage

    Integer idSuperviseurValidateur;

    @NotNull
    StatutDemande statut;

    BigDecimal montantInitial;

    String motif;

    String motifRejet;

    Instant dateDemande;

    Instant dateValidation;

    // Informations du client (pour l'affichage)
    String nomClient;
    String prenomClient;
    String emailClient;
}

