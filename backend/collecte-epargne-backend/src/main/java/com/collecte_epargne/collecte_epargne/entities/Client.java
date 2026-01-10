package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.TypeCNI;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client {
    @Id
    @Size(max = 50)
    @Column(name = "CODE_CLIENT", nullable = false, length = 50)
    private String codeClient;

    @Size(max = 50)
    @NotNull
    @Column(name = "NUMERO_CLIENT", nullable = false, length = 50)
    private String numeroClient;

    @Size(max = 255)
    @Column(name = "ADRESSE")
    private String adresse;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE_CNI", nullable = false, length = 50)
    private TypeCNI typeCni;

    @Size(max = 50)
    @Column(name = "NUM_CNI", length = 50)
    private String numCni;

    @Size(max = 255)
    @Column(name = "PHOTO_PATH")
    private String photoPath;

    @Size(max = 255)
    @Column(name = "CNI_RECTO_PATH")
    private String cniRectoPath;

    @Size(max = 255)
    @Column(name = "CNI_VERSO_PATH")
    private String cniVersoPath;

    @Column(name = "DATE_NAISSANCE")
    private LocalDate dateNaissance;

    @Size(max = 100)
    @Column(name = "LIEU_NAISSANCE", length = 100)
    private String lieuNaissance;

    @Size(max = 100)
    @Column(name = "PROFESSION", length = 100)
    private String profession;

    @Column(name = "SCORE_EPARGNE")
    private Integer scoreEpargne;

    // Remplacer LOGIN brut par la relation OneToOne vers Utilisateur
    @OneToOne(fetch = FetchType.EAGER, optional = false) // EAGER pour éviter les problèmes de lazy loading
    @JoinColumn(name = "LOGIN", referencedColumnName = "LOGIN", nullable = false, unique = true) // LOGIN est FK vers utilisateur.LOGIN
    private Utilisateur utilisateur;

    // Remplacer COLLECTEUR_ASSIGNE par la relation ManyToOne vers Employe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTEUR_ASSIGNE")
    private Employe collecteurAssigne; // Le collecteur affecté

    // Relation ManyToOne vers AgenceZone (un client appartient à une agence)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AGENCE")
    private AgenceZone agence; // L'agence à laquelle appartient le client

    // Relation OneToMany vers Compte (un client a plusieurs comptes)
    @OneToMany(mappedBy = "client")
    private Set<Compte> comptes;

    // Relation OneToMany vers DemandeOuvertureCompte
    @OneToMany(mappedBy = "client")
    private Set<DemandeOuvertureCompte> demandesOuverture;

}