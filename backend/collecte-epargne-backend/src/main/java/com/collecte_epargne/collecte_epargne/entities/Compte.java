package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutCompte;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;


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

    public Compte() {

    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public String getNumCompte() {
        return numCompte;
    }

    public void setNumCompte(String numCompte) {
        this.numCompte = numCompte;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public BigDecimal getSoldeDisponible() {
        return soldeDisponible;
    }

    public void setSoldeDisponible(BigDecimal soldeDisponible) {
        this.soldeDisponible = soldeDisponible;
    }

    public LocalDate getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDate dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Instant getDateDerniereTransaction() {
        return dateDerniereTransaction;
    }

    public void setDateDerniereTransaction(Instant dateDerniereTransaction) {
        this.dateDerniereTransaction = dateDerniereTransaction;
    }

    public BigDecimal getTauxPenalite() {
        return tauxPenalite;
    }

    public void setTauxPenalite(BigDecimal tauxPenalite) {
        this.tauxPenalite = tauxPenalite;
    }

    public BigDecimal getTauxBonus() {
        return tauxBonus;
    }

    public void setTauxBonus(BigDecimal tauxBonus) {
        this.tauxBonus = tauxBonus;
    }

    public StatutCompte getStatut() {
        return statut;
    }

    public void setStatut(StatutCompte statut) {
        this.statut = statut;
    }

    public String getMotifBlocage() {
        return motifBlocage;
    }

    public void setMotifBlocage(String motifBlocage) {
        this.motifBlocage = motifBlocage;
    }

    public LocalDate getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDate dateCloture) {
        this.dateCloture = dateCloture;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public TypeCompte getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(TypeCompte typeCompte) {
        this.typeCompte = typeCompte;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<CompteCotisation> getPlansDeCotisation() {
        return plansDeCotisation;
    }

    public void setPlansDeCotisation(Set<CompteCotisation> plansDeCotisation) {
        this.plansDeCotisation = plansDeCotisation;
    }

    public Compte(String idCompte, String numCompte, BigDecimal solde, BigDecimal soldeDisponible, LocalDate dateOuverture, Instant dateDerniereTransaction, BigDecimal tauxPenalite, BigDecimal tauxBonus, StatutCompte statut, String motifBlocage, LocalDate dateCloture, Client client, TypeCompte typeCompte, Set<Transaction> transactions, Set<CompteCotisation> plansDeCotisation) {
        this.idCompte = idCompte;
        this.numCompte = numCompte;
        this.solde = solde;
        this.soldeDisponible = soldeDisponible;
        this.dateOuverture = dateOuverture;
        this.dateDerniereTransaction = dateDerniereTransaction;
        this.tauxPenalite = tauxPenalite;
        this.tauxBonus = tauxBonus;
        this.statut = statut;
        this.motifBlocage = motifBlocage;
        this.dateCloture = dateCloture;
        this.client = client;
        this.typeCompte = typeCompte;
        this.transactions = transactions;
        this.plansDeCotisation = plansDeCotisation;
    }
}