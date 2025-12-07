package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
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

}