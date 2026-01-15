
package com.collecte_epargne.collecte_epargne.dtos;

public class LoginResponse {

    private String token;
    private String type = "Bearer";

    private String login;
    private String email;
    private String role;

    private String nom;
    private String prenom;

    private String telephone;
    private Integer idRole;
    private String codeRole;
    private Integer idAgence;

    public LoginResponse() {
    }

    public LoginResponse(String token, String type, String login, String email, String nom, String prenom, String role, String telephone, Integer idRole, String codeRole, Integer idAgence) {
        this.token = token;
        this.type = type;
        this.login = login;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.telephone = telephone;
        this.idRole = idRole;
        this.codeRole = codeRole;
        this.idAgence = idAgence;
    }
    
    // Constructor matching AuthController usage (legacy/current) but fixed?
    // AuthController line 53: new LoginResponse(token, "Bearer", login, email, nom, prenom, roleName)
    // Adding separate constructor for backward compatibility or updating AuthController
    public LoginResponse(String token, String type, String login, String email, String nom, String prenom, String role) {
         this(token, type, login, email, nom, prenom, role, null, null, null, null);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Integer getIdRole() { return idRole; }
    public void setIdRole(Integer idRole) { this.idRole = idRole; }

    public String getCodeRole() { return codeRole; }
    public void setCodeRole(String codeRole) { this.codeRole = codeRole; }

    public Integer getIdAgence() { return idAgence; }
    public void setIdAgence(Integer idAgence) { this.idAgence = idAgence; }
}
