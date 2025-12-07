package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "type_compte")
public class TypeCompte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TYPE", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "CODE", nullable = false, length = 20)
    private String code;

    @Size(max = 50)
    @NotNull
    @Column(name = "NOM", nullable = false, length = 50)
    private String nom;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TAUX_INTERET", precision = 5, scale = 2)
    private BigDecimal tauxInteret;

    @Column(name = "SOLDE_MINIMUM", precision = 15, scale = 2)
    private BigDecimal soldeMinimum;

    @Column(name = "FRAIS_OUVERTURE", precision = 15, scale = 2)
    private BigDecimal fraisOuverture;

    @Column(name = "FRAIS_CLOTURE", precision = 15, scale = 2)
    private BigDecimal fraisCloture;

    @Column(name = "AUTORISER_RETRAIT")
    private Boolean autoriserRetrait;

    @Column(name = "DUREE_BLOCAGE_JOURS")
    private Integer dureeBlocageJours;

}