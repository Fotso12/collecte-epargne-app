package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.EmailRequest;
import com.collecte_epargne.collecte_epargne.dtos.VerifyCodeRequest;
import com.collecte_epargne.collecte_epargne.dtos.ResetPasswordRequest;
import com.collecte_epargne.collecte_epargne.services.interfaces.AuthServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/password")
public class PasswordResetController {

    private final AuthServiceInterface authService;

    public PasswordResetController(AuthServiceInterface authService) {
        this.authService = authService;
    }

    // Envoi du code par email
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest request) {
        try {
            authService.envoyerCode(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "Code envoyé par email"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Vérification du code
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        try {
            authService.verifierCode(request.getEmail(), request.getCode());
            return ResponseEntity.ok(Map.of("message", "Code valide"));
        } catch (RuntimeException e) {
            // Renvoie 400 Bad Request au lieu de 500
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Nouveau mot de passe
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        // Extraction manuelle des données du JSON
        String email = payload.get("email");
        String password = payload.get("password");

        // LOG DE VÉRITÉ : Si ça affiche null ici, c'est qu'Angular n'envoie rien
        System.out.println("DEBUG RECU -> Email: [" + email + "] | Pwd: [" + password + "]");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Le serveur n'a reçu aucune donnée (email null)"));
        }

        try {
            authService.resetPassword(email, password);
            return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}