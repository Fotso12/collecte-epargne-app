package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class DeviceTokenDto implements Serializable {
    private Long idDeviceToken;

    @NotNull
    @Size(max = 255)
    private String token;

    private Boolean actif;

    @Size(max = 50)
    private String deviceType;

    @NotNull
    private String loginUtilisateur;

    public DeviceTokenDto() {
    }

    public DeviceTokenDto(Long idDeviceToken, String token, Boolean actif, String deviceType, String loginUtilisateur) {
        this.idDeviceToken = idDeviceToken;
        this.token = token;
        this.actif = actif;
        this.deviceType = deviceType;
        this.loginUtilisateur = loginUtilisateur;
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

    public String getLoginUtilisateur() {
        return loginUtilisateur;
    }

    public void setLoginUtilisateur(String loginUtilisateur) {
        this.loginUtilisateur = loginUtilisateur;
    }
}
