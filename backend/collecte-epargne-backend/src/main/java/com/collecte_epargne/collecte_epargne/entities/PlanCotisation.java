package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.FrequenceCotisation;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getMontantAttendu() {
        return montantAttendu;
    }

    public void setMontantAttendu(BigDecimal montantAttendu) {
        this.montantAttendu = montantAttendu;
    }

    public FrequenceCotisation getFrequence() {
        return frequence;
    }

    public void setFrequence(FrequenceCotisation frequence) {
        this.frequence = frequence;
    }

    public Integer getDureeJours() {
        return dureeJours;
    }

    public void setDureeJours(Integer dureeJours) {
        this.dureeJours = dureeJours;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getTauxPenaliteRetard() {
        return tauxPenaliteRetard;
    }

    public void setTauxPenaliteRetard(BigDecimal tauxPenaliteRetard) {
        this.tauxPenaliteRetard = tauxPenaliteRetard;
    }

    public StatutPlanCotisation getStatut() {
        return statut;
    }

    public void setStatut(StatutPlanCotisation statut) {
        this.statut = statut;
    }

    public PlanCotisation(String idPlan, String nom, BigDecimal montantAttendu, FrequenceCotisation frequence, Integer dureeJours, LocalDate dateDebut, LocalDate dateFin, BigDecimal tauxPenaliteRetard, StatutPlanCotisation statut) {
        this.idPlan = idPlan;
        this.nom = nom;
        this.montantAttendu = montantAttendu;
        this.frequence = frequence;
        this.dureeJours = dureeJours;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tauxPenaliteRetard = tauxPenaliteRetard;
        this.statut = statut;
    }
}