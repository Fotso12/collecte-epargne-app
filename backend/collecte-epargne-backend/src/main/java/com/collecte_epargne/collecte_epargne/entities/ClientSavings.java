package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Entité Client selon la structure savings_collector.sql
 * Séparée de User - les clients ne sont pas des utilisateurs du système
 */
@Entity
@Table(name = "clients")
public class ClientSavings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collector_id")
    private Utilisateur collector; // Référence vers le collecteur (user)

    @Size(max = 150)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Size(max = 40)
    @Column(name = "phone", length = 40)
    private String phone;

    @Size(max = 40)
    @Column(name = "identity_type", length = 40)
    private String identityType; // CNI, Passport, etc.

    @Size(max = 80)
    @Column(name = "identity_number", length = 80)
    private String identityNumber;

    @Lob
    @Column(name = "address")
    private String address;

    @Size(max = 255)
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "date_naissance")
    private java.time.LocalDate dateNaissance;

    @Size(max = 100)
    @Column(name = "lieu_naissance", length = 100)
    private String lieuNaissance;

    @Size(max = 100)
    @Column(name = "profession", length = 100)
    private String profession;

    @Column(name = "score_epargne")
    private Integer scoreEpargne;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ClientStatus status = ClientStatus.ACTIVE;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (status == null) {
            status = ClientStatus.ACTIVE;
        }
    }

    public enum ClientStatus {
        ACTIVE, SUSPENDED, CLOSED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Utilisateur getCollector() {
        return collector;
    }

    public void setCollector(Utilisateur collector) {
        this.collector = collector;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(java.time.LocalDate dateNaissance) {
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
}

