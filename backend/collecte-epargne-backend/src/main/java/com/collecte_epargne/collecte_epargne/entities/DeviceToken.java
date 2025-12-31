package com.collecte_epargne.collecte_epargne.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "device_token")
public class DeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEVICE_TOKEN")
    private Long idDeviceToken;

    @NotNull
    @Size(max = 255)
    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @NotNull
    @Column(name = "ACTIF", nullable = false)
    private Boolean actif;

    @Size(max = 50)
    @Column(name = "DEVICE_TYPE")
    private String deviceType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOGIN_UTILISATEUR", nullable = false)
    private Utilisateur utilisateur;

    public DeviceToken() {
    }

    public DeviceToken(String token, Boolean actif, String deviceType, Utilisateur utilisateur) {
        this.token = token;
        this.actif = actif;
        this.deviceType = deviceType;
        this.utilisateur = utilisateur;
    }

    public Long getIdDeviceToken() {
        return idDeviceToken;
    }

    public void setIdDeviceToken(Long idDeviceToken) {
        this.idDeviceToken = idDeviceToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
