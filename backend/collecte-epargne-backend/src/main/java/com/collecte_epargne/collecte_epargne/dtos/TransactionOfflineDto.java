package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO pour TransactionOffline
 */
@Value
public class TransactionOfflineDto implements Serializable {
    @Size(max = 50)
    String idOffline;

    @NotNull
    BigDecimal montant;

    @NotNull
    TypeTransaction typeTransaction;

    @NotNull
    Instant dateTransaction;

    String description;

    String signatureClient;

    BigDecimal latitude;

    BigDecimal longitude;

    StatutSynchroOffline statutSynchro;

    Instant dateSynchro;

    String erreurSynchro;

    // Remplacer Employe par son ID_EMPLOYE
    @NotNull
    String idEmploye;

    // Remplacer Client par son CODE_CLIENT
    @NotNull
    String codeClient;

    // Remplacer Compte par son ID_COMPTE
    @NotNull
    String idCompte;

    // Remplacer Transaction par son ID_TRANSACTION
    String idTransactionFinale;
}