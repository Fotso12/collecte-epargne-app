package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.FormatRecu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "recu")
public class Recu {
    @Id
    @Size(max = 50)
    @Column(name = "ID_RECU", nullable = false, length = 50)
    private String idRecu;

    // Par la relation OneToOne (doit être nommé "transaction" pour correspondre au mappedBy)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TRANSACTION", referencedColumnName = "ID_TRANSACTION", nullable = false)
    private Transaction transaction;

    @NotNull
    @Lob
    @Column(name = "FORMAT", nullable = false)
    private FormatRecu format;

    @Lob
    @Column(name = "CONTENU")
    private String contenu;

    @Size(max = 255)
    @Column(name = "FICHIER_PATH")
    private String fichierPath;

    @Column(name = "DATE_GENERATION")
    private Instant dateGeneration;

}