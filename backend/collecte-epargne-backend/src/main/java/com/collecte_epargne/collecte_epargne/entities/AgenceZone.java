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
@Table(name = "agence_zone")
public class AgenceZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AGENCE", nullable = false)
    private Integer idAgence;

    @Size(max = 50)
    @NotNull
    @Column(name = "CODE", nullable = false, length = 50)
    private String code;

    @Size(max = 100)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 100)
    private String nom;

    @Size(max = 50)
    @Column(name = "VILLE", length = 50)
    private String ville;

    @Size(max = 50)
    @Column(name = "QUARTIER", length = 50)
    private String quartier;

    @Size(max = 255)
    @Column(name = "ADRESSE")
    private String adresse;

    @Size(max = 20)
    @Column(name = "TELEPHONE", length = 20)
    private String telephone;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "STATUT")
    private StatutGenerique statut;

    @Column(name = "DATE_CREATION")
    private Instant dateCreation;

}