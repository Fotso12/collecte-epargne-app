package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour AgenceZone
 */
@Value
public class AgenceZoneDto implements Serializable {
    @Size(max = 50)
    String idAgence;

    @Size(max = 50)
    @NotNull
    String code;

    @Size(max = 100)
    @NotNull
    String nom;

    @Size(max = 50)
    String ville;

    @Size(max = 50)
    String quartier;

    @Size(max = 255)
    String adresse;

    @Size(max = 20)
    String telephone;

    String description;

    StatutGenerique statut;

    Instant dateCreation;
}