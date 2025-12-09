package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO pour TypeCompte
 */
@Value
public class TypeCompteDto implements Serializable {
    Integer id;

    @Size(max = 20)
    @NotNull
    String code;

    @Size(max = 50)
    @NotNull
    String nom;

    String description;

    BigDecimal tauxInteret;

    BigDecimal soldeMinimum;

    BigDecimal fraisOuverture;

    BigDecimal fraisCloture;

    Boolean autoriserRetrait;

    Integer dureeBlocageJours;
}