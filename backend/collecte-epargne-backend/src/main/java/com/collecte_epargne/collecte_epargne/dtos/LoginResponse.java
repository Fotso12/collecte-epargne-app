
package com.collecte_epargne.collecte_epargne.dtos;

public class LoginResponse {

    private String token;
    private String type = "Bearer";

    private String login;
    private String email;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(String token, String login, String email, String role) {
        this.token = token;
        this.login = login;
        this.email = email;
        this.role = role;
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
