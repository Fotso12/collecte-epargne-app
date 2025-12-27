package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.FormatRecu;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;


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

    public RecuDto(String idRecu, String idTransaction, FormatRecu format, String contenu, String fichierPath, Instant dateGeneration) {
        this.idRecu = idRecu;
        this.idTransaction = idTransaction;
        this.format = format;
        this.contenu = contenu;
        this.fichierPath = fichierPath;
        this.dateGeneration = dateGeneration;
    }

    public String getIdRecu() {
        return idRecu;
    }

    public RecuDto() {
    }

    public void setIdRecu(String idRecu) {
        this.idRecu = idRecu;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
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
}