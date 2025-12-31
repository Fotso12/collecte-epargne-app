package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;


public class UtilisateurDto implements Serializable {
    @Size(max = 50)
    @NotNull
    private String login;

    @NotNull
    private Integer idRole;

    // --- Champs Relationnels (ajoutés pour le DTO complet) ---
    private String idEmploye;
    private String codeClient;
    // --------------------------------------------------------

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

    // Le champ 'password' est EXCLU de ce DTO de sortie/mise à jour

    private StatutGenerique statut;

    private Instant dateCreation;

    public UtilisateurDto(String login, Integer idRole, String idEmploye, String codeClient, String nom, String prenom, String telephone, String email, StatutGenerique statut, Instant dateCreation) {
        this.login = login;
        this.idRole = idRole;
        this.idEmploye = idEmploye;
        this.codeClient = codeClient;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public UtilisateurDto() {
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

    public String getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(String idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
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
}