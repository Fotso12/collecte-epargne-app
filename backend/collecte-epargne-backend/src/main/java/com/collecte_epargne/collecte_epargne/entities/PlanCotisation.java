package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.FrequenceCotisation;
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
@Table(name = "plan_cotisation")
public class PlanCotisation {
    @Id
    @Size(max = 50)
    @Column(name = "ID_PLAN", nullable = false, length = 50)
    private String idPlan;

    @Size(max = 100)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 100)
    private String nom;

    @NotNull
    @Column(name = "MONTANT_ATTENDU", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantAttendu;

    @NotNull
    @Lob
    @Column(name = "FREQUENCE", nullable = false)
    private FrequenceCotisation frequence;

    @Column(name = "DUREE_JOURS")
    private Integer dureeJours;

    @NotNull
    @Column(name = "DATE_DEBUT", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "DATE_FIN")
    private LocalDate dateFin;

    @Column(name = "TAUX_PENALITE_RETARD", precision = 5, scale = 2)
    private BigDecimal tauxPenaliteRetard;

    @Lob
    @Column(name = "STATUT")
    private StatutPlanCotisation statut;

}