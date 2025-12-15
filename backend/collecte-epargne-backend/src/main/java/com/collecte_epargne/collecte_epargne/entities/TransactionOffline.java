package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transaction_offline")
public class TransactionOffline {
    @Id
    @Size(max = 50)
    @Column(name = "ID_OFFLINE", nullable = false, length = 50)
    private String idOffline;

    @NotNull
    @Column(name = "MONTANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @NotNull
    @Lob
    @Column(name = "TYPE_TRANSACTION", nullable = false)
    private TypeTransaction typeTransaction;

    @NotNull
    @Column(name = "DATE_TRANSACTION", nullable = false)
    private Instant dateTransaction;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "SIGNATURE_CLIENT")
    private String signatureClient;

    @Column(name = "LATITUDE", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Lob
    @Column(name = "STATUT_SYNCHRO")
    private StatutSynchroOffline statutSynchro;

    @Column(name = "DATE_SYNCHRO")
    private Instant dateSynchro;

    @Lob
    @Column(name = "ERREUR_SYNCHRO")
    private String erreurSynchro;

    // Remplacer ID_EMPLOYE par la relation ManyToOne vers Employe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPLOYE", nullable = false)
    private Employe employe;

    // Remplacer CODE_CLIENT (même si on a déjà l'ID_COMPTE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_CLIENT", nullable = false)
    private Client client;

    // Remplacer ID_COMPTE par la relation ManyToOne vers Compte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPTE", nullable = false)
    private Compte compte;

    // Remplacer ID_TRANSACTION_FINALE par la relation ManyToOne vers Transaction
    @OneToOne(fetch = FetchType.LAZY) // Une transaction offline est complétée par une seule Transaction finale
    @JoinColumn(name = "ID_TRANSACTION_FINALE")
    private Transaction transactionFinale;

}