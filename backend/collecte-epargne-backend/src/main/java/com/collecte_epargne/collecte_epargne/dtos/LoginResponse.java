package com.collecte_epargne.collecte_epargne.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String login;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Integer idRole;
    private String codeRole;
    private String nomRole;
    private String message;
}

