package com.collecte_epargne.collecte_epargne.entities;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String code;

    private LocalDateTime expirationTime;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public PasswordResetCode(Long id, String email, String code, LocalDateTime expirationTime) {
        this.id = id;
        this.email = email;
        this.code = code;
        this.expirationTime = expirationTime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public PasswordResetCode() {
    }
    // getters et setters
}
