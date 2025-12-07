package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.CategorieNotification;
import com.collecte_epargne.collecte_epargne.utils.TypeNotification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @Size(max = 50)
    @Column(name = "ID_NOTIFICATION", nullable = false, length = 50)
    private String idNotification;

    @Size(max = 50)
    @Column(name = "CODE_CLIENT", length = 50)
    private String codeClient;

    // Par la relation ManyToOne (doit être nommé "transaction" pour correspondre au mappedBy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TRANSACTION") // ID_TRANSACTION est la colonne FK dans la table NOTIFICATION
    private Transaction transaction;

    @NotNull
    @Lob
    @Column(name = "TYPE", nullable = false)
    private TypeNotification type;

    @NotNull
    @Lob
    @Column(name = "CATEGORIE", nullable = false)
    private CategorieNotification categorie;

    @Size(max = 100)
    @Column(name = "TITRE", length = 100)
    private String titre;

    @NotNull
    @Lob
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Lob
    @Column(name = "STATUT")
    private String statut;

    @Column(name = "DATE_CREATION")
    private Instant dateCreation;

    @Column(name = "DATE_ENVOI")
    private Instant dateEnvoi;

    @Column(name = "DATE_LECTURE")
    private Instant dateLecture;

    @Lob
    @Column(name = "ERREUR_ENVOI")
    private String erreurEnvoi;

}