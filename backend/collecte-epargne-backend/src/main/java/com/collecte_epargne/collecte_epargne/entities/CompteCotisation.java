package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "compte_cotisation")
public class CompteCotisation {
    @Id
    @Size(max = 50)
    @Column(name = "ID", nullable = false, length = 50)
    private String id;

    @NotNull
    @Column(name = "DATE_ADHESION", nullable = false)
    private LocalDate dateAdhesion;

    @Column(name = "MONTANT_TOTAL_VERSE", precision = 15, scale = 2)
    private BigDecimal montantTotalVerse;

    @Column(name = "NOMBRE_VERSEMENTS")
    private Integer nombreVersements;

    @Column(name = "NOMBRE_RETARDS")
    private Integer nombreRetards;

    @Column(name = "PROCHAINE_ECHEANCE")
    private LocalDate prochaineEcheance;

    @Lob
    @Column(name = "STATUT")
    private StatutPlanCotisation statut;

    // Remplacer ID_COMPTE par la relation ManyToOne vers Compte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPTE", nullable = false)
    private Compte compte;

    // Remplacer ID_PLAN par la relation ManyToOne vers PlanCotisation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PLAN", nullable = false)
    private PlanCotisation planCotisation;

}