package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.Set;


@Entity

@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROLE", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "CODE", nullable = false, length = 20)
    private String code;

    @Size(max = 50)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 50)
    private String nom;

    @Size(max = 255)
    @Column(name = "DESCRIPTION")
    private String description;

    // Relation OneToMany vers Utilisateur (Role est référencé par plusieurs Utilisateurs)
    @OneToMany(mappedBy = "role") // 'role' est le nom du champ ManyToOne dans Utilisateur.java
    private Set<Utilisateur> utilisateurs;

    public Role() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(Set<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    public Role(Integer id, String code, String nom, String description, Set<Utilisateur> utilisateurs) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.utilisateurs = utilisateurs;
    }

    public String getLibelle() {
        return null;
    }
}