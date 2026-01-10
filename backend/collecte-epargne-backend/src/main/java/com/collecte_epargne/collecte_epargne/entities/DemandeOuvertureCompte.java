package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutDemande;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "demande_ouverture_compte")
public class DemandeOuvertureCompte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEMANDE", nullable = false)
    private Long idDemande;

    // Relation ManyToOne vers Client
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CODE_CLIENT", nullable = false)
    private Client client;

    // Relation ManyToOne vers TypeCompte
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TYPE_COMPTE", nullable = false)
    private TypeCompte typeCompte;

    // Relation ManyToOne vers Employe (superviseur qui valide)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUPERVISEUR_VALIDATEUR")
    private Employe superviseurValidateur;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUT", nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(name = "MONTANT_INITIAL", precision = 15, scale = 2)
    private BigDecimal montantInitial;

    @Lob
    @Column(name = "MOTIF")
    private String motif; // Motif de la demande du client

    @Lob
    @Column(name = "MOTIF_REJET")
    private String motifRejet; // Motif de rejet si rejetée

    @Column(name = "DATE_DEMANDE", nullable = false)
    private Instant dateDemande;

    @Column(name = "DATE_VALIDATION")
    private Instant dateValidation;

    @PrePersist
    protected void onCreate() {
        if (dateDemande == null) {
            dateDemande = Instant.now();
        }
        if (statut == null) {
            statut = StatutDemande.EN_ATTENTE;
        }
    }
}

