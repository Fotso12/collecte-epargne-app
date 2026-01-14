package com.collecte_epargne.collecte_epargne.dtos;


public class EmailRequest {

    private String email;

    // Constructeur vide (OBLIGATOIRE pour Spring)
    public EmailRequest() {
    }

    // Getter
    public String getEmail() {
        return email;
    }

    // Setter
    public void setEmail(String email) {
        this.email = email;
    }
}
