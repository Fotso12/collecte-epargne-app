package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.ModeTransaction;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @Size(max = 50)
    @Column(name = "ID_TRANSACTION", nullable = false, length = 50)
    private String idTransaction;

    // Remplacer ID_COMPTE par la relation ManyToOne vers Compte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPTE", nullable = false)
    private Compte compte;

    // Les 3 références à EMPLOYE (Initiateur, Caissier, Superviseur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPLOYE_INITIATEUR")
    private Employe initiateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CAISSIER_VALIDATEUR")
    private Employe caissierValidateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUPERVISEUR_VALIDATEUR")
    private Employe superviseurValidateur;

    // Relation OneToOne vers Recu et Notification (si l'inverse est nécessaire)
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recu recu;

    @OneToMany(mappedBy = "transaction")
    private Set<Notification> notifications;

    @Size(max = 50)
    @NotNull
    @Column(name = "REFERENCE", nullable = false, length = 50)
    private String reference;

    @NotNull
    @Lob
    @Column(name = "TYPE_TRANSACTION", nullable = false)
    private TypeTransaction typeTransaction;

    @NotNull
    @Column(name = "MONTANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @NotNull
    @Column(name = "SOLDE_AVANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeAvant;

    @NotNull
    @Column(name = "SOLDE_APRES", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeApres;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATE_TRANSACTION")
    private Instant dateTransaction;

    @Column(name = "DATE_VALIDATION_CAISSE")
    private Instant dateValidationCaisse;

    @Column(name = "DATE_VALIDATION_SUPERVISEUR")
    private Instant dateValidationSuperviseur;

    @Lob
    @Column(name = "MOTIF_REJET")
    private String motifRejet;

    @Lob
    @Column(name = "STATUT")
    private StatutTransaction statut;

    @Lob
    @Column(name = "MODE_TRANSACTION")
    private ModeTransaction modeTransaction;

    @Lob
    @Column(name = "SIGNATURE_CLIENT")
    private String signatureClient;

    @Size(max = 255)
    @Column(name = "HASH_TRANSACTION")
    private String hashTransaction;

}