package com.collecte_epargne.collecte_epargne.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
public class ResetPasswordRequest {
    private String email;
    private String password; // Vérifie si tu as nommé ce champ 'password' ou 'newPassword'

    // Getters et Setters (INDISPENSABLES pour Spring)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public ResetPasswordRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public ResetPasswordRequest() {
    }
}

