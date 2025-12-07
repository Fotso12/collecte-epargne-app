package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutCompte;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "compte")
public class Compte {
    @Id
    @Size(max = 50)
    @Column(name = "ID_COMPTE", nullable = false, length = 50)
    private String idCompte;

    @Size(max = 50)
    @NotNull
    @Column(name = "NUM_COMPTE", nullable = false, length = 50)
    private String numCompte;

    @Column(name = "SOLDE", precision = 15, scale = 2)
    private BigDecimal solde;

    @Column(name = "SOLDE_DISPONIBLE", precision = 15, scale = 2)
    private BigDecimal soldeDisponible;

    @NotNull
    @Column(name = "DATE_OUVERTURE", nullable = false)
    private LocalDate dateOuverture;

    @Column(name = "DATE_DERNIERE_TRANSACTION")
    private Instant dateDerniereTransaction;

    @Column(name = "TAUX_PENALITE", precision = 5, scale = 2)
    private BigDecimal tauxPenalite;

    @Column(name = "TAUX_BONUS", precision = 5, scale = 2)
    private BigDecimal tauxBonus;

    @Lob
    @Column(name = "STATUT")
    private StatutCompte statut;

    @Lob
    @Column(name = "MOTIF_BLOCAGE")
    private String motifBlocage;

    @Column(name = "DATE_CLOTURE")
    private LocalDate dateCloture;

    // Remplacer CODE_CLIENT par la relation ManyToOne vers Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_CLIENT", nullable = false)
    private Client client;

    // Remplacer ID_TYPE par la relation ManyToOne vers TypeCompte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TYPE", nullable = false)
    private TypeCompte typeCompte;

    // Relations OneToMany
    @OneToMany(mappedBy = "compte")
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "compte")
    private Set<CompteCotisation> plansDeCotisation;

}