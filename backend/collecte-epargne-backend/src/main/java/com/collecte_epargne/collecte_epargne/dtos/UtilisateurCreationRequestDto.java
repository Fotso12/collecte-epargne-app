package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UtilisateurCreationRequestDto {

    @Size(max = 50)
    @NotNull
    private String login;

    @NotNull
    private Integer idRole;

    @Size(max = 50)
    @NotNull
    private String nom;

    @Size(max = 50)
    @NotNull
    private String prenom;

    @Size(max = 20)
    @NotNull
    private String telephone;

    @Size(max = 100)
    @NotNull
    private String email;

    @Size(min = 6, max = 255) // Ajout d'une contrainte de taille pour le password
    @NotNull
    private String password; // Le mot de passe en clair (non haché)

    private StatutGenerique statut;

    public UtilisateurCreationRequestDto(String login, Integer idRole, String nom, String prenom, String telephone, String email, String password, StatutGenerique statut) {
        this.login = login;
        this.idRole = idRole;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.statut = statut;
    }

    public UtilisateurCreationRequestDto() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getIdRole() {
        return idRole;
    }

    public void setIdRole(Integer idRole) {
        this.idRole = idRole;
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
}