package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "institutions")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Size(max = 30)
    @NotNull
    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @Size(max = 120)
    @Column(name = "contact_email", length = 120)
    private String contactEmail;

    @Size(max = 40)
    @Column(name = "contact_phone", length = 40)
    private String contactPhone;

    @Size(max = 64)
    @Column(name = "timezone", length = 64)
    private String timezone = "Africa/Abidjan";

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}

