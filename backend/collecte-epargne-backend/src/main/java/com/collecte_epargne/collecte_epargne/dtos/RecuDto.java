package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.FormatRecu;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour Recu
 */
@Value
public class RecuDto implements Serializable {
    @Size(max = 50)
    String idRecu;

    // Remplacer Transaction par son ID_TRANSACTION
    @NotNull
    String idTransaction;

    @NotNull
    FormatRecu format;

    String contenu;

    @Size(max = 255)
    String fichierPath;

    Instant dateGeneration;
}