package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entité Client selon la structure savings_collector.sql
 * Séparée de User - les clients ne sont pas des utilisateurs du système
 */
@Getter
@Setter
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
}

