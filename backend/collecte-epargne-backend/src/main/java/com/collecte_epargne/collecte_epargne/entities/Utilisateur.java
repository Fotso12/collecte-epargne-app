package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @Size(max = 50)
    @Column(name = "LOGIN", nullable = false, length = 50)
    private String login;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs utilisateurs peuvent avoir le même rôle
    @JoinColumn(name = "ID_ROLE", nullable = false)
    private Role role;

    // Relation OneToOne vers Employe (si Employe a une relation OneToOne avec Utilisateur)
    // On suppose que l'Employé est toujours un Utilisateur
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Employe employe;

    // Relation OneToOne vers Client (un client est un utilisateur du système)
    @OneToOne(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    private Client client;

    @Size(max = 50)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 50)
    private String nom;

    @Size(max = 50)
    @NotNull
    @Column(name = "PRENOM", nullable = false, length = 50)
    private String prenom;

    @Size(max = 20)
    @NotNull
    @Column(name = "TELEPHONE", nullable = false, length = 20)
    private String telephone;

    @Size(max = 100)
    @NotNull
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Lob
    @Column(name = "STATUT")
    @Enumerated(EnumType.STRING)
    private StatutGenerique statut;

    @Column(name = "DATE_CREATION")
    private Instant dateCreation;

}