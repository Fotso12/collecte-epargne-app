package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.LoginRequest;
import com.collecte_epargne.collecte_epargne.dtos.LoginResponse;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;

    public AuthController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Recherche utilisateur par email (staff uniquement)
            Utilisateur user = utilisateurRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

            // Vérification du mot de passe (en clair pour l'instant - NON SÉCURISÉ)
            if (!user.getPassword().equals(loginRequest.getPassword())) {
                throw new RuntimeException("Email ou mot de passe incorrect");
            }

            // Vérifier que l'utilisateur est actif
            if (user.getStatut() != com.collecte_epargne.collecte_epargne.utils.StatutGenerique.ACTIF) {
                throw new RuntimeException("Compte inactif ou suspendu");
            }

            // Créer la réponse avec les infos utilisateur
            LoginResponse response = new LoginResponse(
                    user.getLogin(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getTelephone(),
                    user.getRole().getId(),
                    user.getRole().getCode(),
                    user.getRole().getNom(),
                    "Connexion réussie"
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Connexion client (par téléphone uniquement - pas de mot de passe)
     * Les clients n'ont pas de compte user, donc connexion simplifiée
     */
    @PostMapping("/client-login")
    public ResponseEntity<?> clientLogin(@RequestBody Map<String, String> payload) {
        try {
            String phone = payload.get("phone");
            if (phone == null || phone.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Numéro de téléphone requis"));
            }

            // Recherche client par téléphone
            var clientRepo = applicationContext.getBean(com.collecte_epargne.collecte_epargne.repositories.ClientSavingsRepository.class);
            com.collecte_epargne.collecte_epargne.entities.ClientSavings client = clientRepo.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé avec ce numéro"));

            // Vérifier statut
            if (client.getStatus() != com.collecte_epargne.collecte_epargne.entities.ClientSavings.ClientStatus.ACTIVE) {
                throw new RuntimeException("Compte client inactif");
            }

            // Retourner infos client (format différent car pas de user)
            return ResponseEntity.ok(Map.of(
                    "type", "client",
                    "clientId", client.getId(),
                    "fullName", client.getFullName(),
                    "phone", client.getPhone() != null ? client.getPhone() : "",
                    "message", "Connexion client réussie"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.context.ApplicationContext applicationContext;
}





