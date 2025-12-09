package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.ModeTransaction;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO pour Transaction
 */
@Value
public class TransactionDto implements Serializable {
    @Size(max = 50)
    String idTransaction;

    // Remplacer Compte par son ID_COMPTE
    @NotNull
    String idCompte;

    // Remplacer les 3 Employes par leurs IDs
    String idEmployeInitiateur;
    String idCaissierValidateur;
    String idSuperviseurValidateur;

    // Le reçu (Recu) est souvent chargé séparément si nécessaire

    @Size(max = 50)
    @NotNull
    String reference;

    @NotNull
    TypeTransaction typeTransaction;

    @NotNull
    BigDecimal montant;

    @NotNull
    BigDecimal soldeAvant;

    @NotNull
    BigDecimal soldeApres;

    String description;

    Instant dateTransaction;

    Instant dateValidationCaisse;

    Instant dateValidationSuperviseur;

    String motifRejet;

    StatutTransaction statut;

    ModeTransaction modeTransaction;

    String signatureClient;

    @Size(max = 255)
    String hashTransaction;

    // Notifications omises
}