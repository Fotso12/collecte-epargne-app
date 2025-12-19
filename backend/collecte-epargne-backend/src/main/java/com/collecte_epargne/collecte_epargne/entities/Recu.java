package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.FormatRecu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;


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

    public String getIdRecu() {
        return idRecu;
    }

    public void setIdRecu(String idRecu) {
        this.idRecu = idRecu;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public FormatRecu getFormat() {
        return format;
    }

    public void setFormat(FormatRecu format) {
        this.format = format;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getFichierPath() {
        return fichierPath;
    }

    public void setFichierPath(String fichierPath) {
        this.fichierPath = fichierPath;
    }

    public Instant getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(Instant dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public Recu(String idRecu, Transaction transaction, FormatRecu format, String contenu, String fichierPath, Instant dateGeneration) {
        this.idRecu = idRecu;
        this.transaction = transaction;
        this.format = format;
        this.contenu = contenu;
        this.fichierPath = fichierPath;
        this.dateGeneration = dateGeneration;
    }

    public Recu() {
    }
}