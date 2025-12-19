package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }

    public void setDateAdhesion(LocalDate dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }

    public BigDecimal getMontantTotalVerse() {
        return montantTotalVerse;
    }

    public void setMontantTotalVerse(BigDecimal montantTotalVerse) {
        this.montantTotalVerse = montantTotalVerse;
    }

    public Integer getNombreVersements() {
        return nombreVersements;
    }

    public void setNombreVersements(Integer nombreVersements) {
        this.nombreVersements = nombreVersements;
    }

    public Integer getNombreRetards() {
        return nombreRetards;
    }

    public void setNombreRetards(Integer nombreRetards) {
        this.nombreRetards = nombreRetards;
    }

    public LocalDate getProchaineEcheance() {
        return prochaineEcheance;
    }

    public void setProchaineEcheance(LocalDate prochaineEcheance) {
        this.prochaineEcheance = prochaineEcheance;
    }

    public StatutPlanCotisation getStatut() {
        return statut;
    }

    public void setStatut(StatutPlanCotisation statut) {
        this.statut = statut;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public PlanCotisation getPlanCotisation() {
        return planCotisation;
    }

    public void setPlanCotisation(PlanCotisation planCotisation) {
        this.planCotisation = planCotisation;
    }

    public CompteCotisation(String id, LocalDate dateAdhesion, BigDecimal montantTotalVerse, Integer nombreVersements, Integer nombreRetards, LocalDate prochaineEcheance, StatutPlanCotisation statut, Compte compte, PlanCotisation planCotisation) {
        this.id = id;
        this.dateAdhesion = dateAdhesion;
        this.montantTotalVerse = montantTotalVerse;
        this.nombreVersements = nombreVersements;
        this.nombreRetards = nombreRetards;
        this.prochaineEcheance = prochaineEcheance;
        this.statut = statut;
        this.compte = compte;
        this.planCotisation = planCotisation;
    }

    public CompteCotisation() {
    }
}