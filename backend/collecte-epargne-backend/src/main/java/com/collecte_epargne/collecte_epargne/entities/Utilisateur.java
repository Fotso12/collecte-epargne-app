package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;


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
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    @NotNull
    @Lob
    @Column(name = "STATUT")
    @Enumerated(EnumType.STRING)
    private StatutGenerique statut;

    @Column(name = "DATE_CREATION")
    private Instant dateCreation;

    public Utilisateur() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StatutGenerique getStatut() {
        return statut;
    }

    public void setStatut(StatutGenerique statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Utilisateur(String login, Role role, Employe employe, Client client, String nom, String prenom, String telephone, String email, String password, StatutGenerique statut, Instant dateCreation) {
        this.login = login;
        this.role = role;
        this.employe = employe;
        this.client = client;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }


}