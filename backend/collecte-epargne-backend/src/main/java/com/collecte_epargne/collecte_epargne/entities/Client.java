package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.TypeCNI;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;


@Entity

@Table(name = "client")
public class Client {
    @Id
    @Size(max = 50)
    @Column(name = "CODE_CLIENT", nullable = false, length = 50)
    private String codeClient;

    @Size(max = 50)
    @NotNull
    @Column(name = "NUMERO_CLIENT", nullable = false, length = 50)
    private String numeroClient;

    @NotNull
    @Size(max = 255)
    @Column(name = "ADRESSE")
    private String adresse;

    @NotNull
    @Lob
    @Column(name = "TYPE_CNI", nullable = false)
    private TypeCNI typeCni;

    @Size(max = 50)
    @NotNull
    @Column(name = "NUM_CNI", nullable = false, length = 50)
    private String numCni;

    @NotNull
    @Size(max = 255)
    @Column(name = "PHOTO_PATH")
    private String photoPath;

    @NotNull
    @Size(max = 255)
    @Column(name = "CNI_RECTO_PATH")
    private String cniRectoPath;

    @NotNull
    @Size(max = 255)
    @Column(name = "CNI_VERSO_PATH")
    private String cniVersoPath;

    @NotNull
    @Column(name = "DATE_NAISSANCE")
    private LocalDate dateNaissance;

    @NotNull
    @Size(max = 100)
    @Column(name = "LIEU_NAISSANCE", length = 100)
    private String lieuNaissance;

    @NotNull
    @Size(max = 100)
    @Column(name = "PROFESSION", length = 100)
    private String profession;

    @Column(name = "SCORE_EPARGNE")
    private Integer scoreEpargne;

    // Remplacer LOGIN brut par la relation OneToOne vers Utilisateur
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOGIN", nullable = false, unique = true) // LOGIN est FK
    private Utilisateur utilisateur;

    @NotNull
    // Remplacer COLLECTEUR_ASSIGNE par la relation ManyToOne vers Employe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTEUR_ASSIGNE")
    private Employe collecteurAssigne; // Le collecteur affecté

    // Relation OneToMany vers Compte (un client a plusieurs comptes)
    @OneToMany(mappedBy = "client")
    private Set<Compte> comptes;

    public Client() {

    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public TypeCNI getTypeCni() {
        return typeCni;
    }

    public void setTypeCni(TypeCNI typeCni) {
        this.typeCni = typeCni;
    }

    public String getNumCni() {
        return numCni;
    }

    public void setNumCni(String numCni) {
        this.numCni = numCni;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCniRectoPath() {
        return cniRectoPath;
    }

    public void setCniRectoPath(String cniRectoPath) {
        this.cniRectoPath = cniRectoPath;
    }

    public String getCniVersoPath() {
        return cniVersoPath;
    }

    public void setCniVersoPath(String cniVersoPath) {
        this.cniVersoPath = cniVersoPath;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Integer getScoreEpargne() {
        return scoreEpargne;
    }

    public void setScoreEpargne(Integer scoreEpargne) {
        this.scoreEpargne = scoreEpargne;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Employe getCollecteurAssigne() {
        return collecteurAssigne;
    }

    public void setCollecteurAssigne(Employe collecteurAssigne) {
        this.collecteurAssigne = collecteurAssigne;
    }

    public Set<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(Set<Compte> comptes) {
        this.comptes = comptes;
    }

    public Client(String codeClient, String numeroClient, String adresse, TypeCNI typeCni, String numCni, String photoPath, String cniRectoPath, String cniVersoPath, LocalDate dateNaissance, String lieuNaissance, String profession, Integer scoreEpargne, Utilisateur utilisateur, Employe collecteurAssigne, Set<Compte> comptes) {
        this.codeClient = codeClient;
        this.numeroClient = numeroClient;
        this.adresse = adresse;
        this.typeCni = typeCni;
        this.numCni = numCni;
        this.photoPath = photoPath;
        this.cniRectoPath = cniRectoPath;
        this.cniVersoPath = cniVersoPath;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;
        this.profession = profession;
        this.scoreEpargne = scoreEpargne;
        this.utilisateur = utilisateur;
        this.collecteurAssigne = collecteurAssigne;
        this.comptes = comptes;
    }
}