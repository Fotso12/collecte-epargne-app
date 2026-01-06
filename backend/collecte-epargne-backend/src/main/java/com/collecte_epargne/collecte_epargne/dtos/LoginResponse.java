
package com.collecte_epargne.collecte_epargne.dtos;

public class LoginResponse {

    private String token;
    private String type = "Bearer";

    private String login;
    private String email;
    private String role;

    private String nom;
    private String prenom;

    public LoginResponse() {
    }

    public LoginResponse(String token, String type, String login, String email, String role, String nom, String prenom) {
        this.token = token;
        this.type = type;
        this.login = login;
        this.email = email;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LoginResponse(String token) {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
