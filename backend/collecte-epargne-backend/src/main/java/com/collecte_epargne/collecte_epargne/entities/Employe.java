package com.collecte_epargne.collecte_epargne.entities;



import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "employe")
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPLOYE", nullable = false)
    private Integer idEmploye;


    @Size(max = 50)
    @NotNull
    @Column(name = "MATRICULE", nullable = false, length = 50)
    private String matricule;

    @NotNull
    @Column(name = "DATE_EMBAUCHE", nullable = false)
    private LocalDate dateEmbauche;

    @NotNull
    @Lob
    @Column(name = "TYPE_EMPLOYE", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeEmploye typeEmploye;

    @Column(name = "COMMISSION_TAUX", precision = 5, scale = 2)
    private BigDecimal commissionTaux;


    // Remplacer le LOGIN brut par la relation OneToOne vers Utilisateur
    @OneToOne(fetch = FetchType.EAGER, optional = false) // EAGER pour éviter les problèmes de lazy loading
    @JoinColumn(name = "LOGIN", referencedColumnName = "LOGIN", nullable = false, unique = true) // LOGIN est FK vers utilisateur.LOGIN
    private Utilisateur utilisateur;

    // Remplacer ID_AGENCE par la relation ManyToOne vers AgenceZone
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AGENCE")
    private AgenceZone agenceZone;

    // Relation récursive (SUPERVISEUR_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPERVISEUR_ID")
    private Employe superviseur; // L'employé qui supervise cet employé

    // Relation OneToMany pour l'équipe supervisée
    @OneToMany(mappedBy = "superviseur")
    private Set<Employe> equipeSupervisee; // L'équipe supervisée par cet employé

    // Relation OneToMany pour les clients assignés (implémenté dans Client.java)
    @OneToMany(mappedBy = "collecteurAssigne")
    private Set<Client> clientsAssignes;
}