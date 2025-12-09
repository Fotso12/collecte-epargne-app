package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.CategorieNotification;
import com.collecte_epargne.collecte_epargne.utils.TypeNotification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour Notification
 */
@Value
public class NotificationDto implements Serializable {
    @Size(max = 50)
    String idNotification;

    @Size(max = 50)
    String codeClient;

    // Remplacer Transaction par son ID_TRANSACTION
    String idTransaction;

    @NotNull
    TypeNotification type;

    @NotNull
    CategorieNotification categorie;

    @Size(max = 100)
    String titre;

    @NotNull
    String message;

    String statut;

    Instant dateCreation;

    Instant dateEnvoi;

    Instant dateLecture;

    String erreurEnvoi;
}