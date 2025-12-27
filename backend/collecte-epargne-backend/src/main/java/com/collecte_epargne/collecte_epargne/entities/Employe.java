package com.collecte_epargne.collecte_epargne.entities;



import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOGIN", nullable = false, unique = true) // LOGIN est PK/FK
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

    public Employe() {

    }

    public Integer getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public TypeEmploye getTypeEmploye() {
        return typeEmploye;
    }

    public void setTypeEmploye(TypeEmploye typeEmploye) {
        this.typeEmploye = typeEmploye;
    }

    public BigDecimal getCommissionTaux() {
        return commissionTaux;
    }

    public void setCommissionTaux(BigDecimal commissionTaux) {
        this.commissionTaux = commissionTaux;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public AgenceZone getAgenceZone() {
        return agenceZone;
    }

    public void setAgenceZone(AgenceZone agenceZone) {
        this.agenceZone = agenceZone;
    }

    public Employe getSuperviseur() {
        return superviseur;
    }

    public void setSuperviseur(Employe superviseur) {
        this.superviseur = superviseur;
    }

    public Set<Employe> getEquipeSupervisee() {
        return equipeSupervisee;
    }

    public void setEquipeSupervisee(Set<Employe> equipeSupervisee) {
        this.equipeSupervisee = equipeSupervisee;
    }

    public Set<Client> getClientsAssignes() {
        return clientsAssignes;
    }

    public void setClientsAssignes(Set<Client> clientsAssignes) {
        this.clientsAssignes = clientsAssignes;
    }

    public Employe(Integer idEmploye, String matricule, LocalDate dateEmbauche, TypeEmploye typeEmploye, BigDecimal commissionTaux, Utilisateur utilisateur, AgenceZone agenceZone, Employe superviseur, Set<Employe> equipeSupervisee, Set<Client> clientsAssignes) {
        this.idEmploye = idEmploye;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.typeEmploye = typeEmploye;
        this.commissionTaux = commissionTaux;
        this.utilisateur = utilisateur;
        this.agenceZone = agenceZone;
        this.superviseur = superviseur;
        this.equipeSupervisee = equipeSupervisee;
        this.clientsAssignes = clientsAssignes;
    }

}