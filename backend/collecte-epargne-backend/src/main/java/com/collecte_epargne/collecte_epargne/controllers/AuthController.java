package com.collecte_epargne.collecte_epargne.controllers;
import com.collecte_epargne.collecte_epargne.dtos.LoginRequest;
import com.collecte_epargne.collecte_epargne.dtos.LoginResponse;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UtilisateurRepository utilisateurRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * LOGIN
     * email + mot de passe
     * retourne un JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        // 1️⃣ Authentification
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2️⃣ Récupération des détails utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 3️⃣ Génération du token
        String token = jwtService.generateToken(request.getEmail());

                // 4️⃣ Retour du token avec les détails utilisateur + éventuel idEmploye et idAgence
                Integer idEmploye = null;
                Integer idAgence = null;
                if (utilisateur.getEmploye() != null) {
                        idEmploye = utilisateur.getEmploye().getIdEmploye();
                        if (utilisateur.getEmploye().getAgenceZone() != null) {
                                idAgence = utilisateur.getEmploye().getAgenceZone().getIdAgence();
                        }
                }

                return ResponseEntity.ok(new LoginResponse(
                                token,
                                "Bearer",
                                utilisateur.getLogin(),
                                utilisateur.getEmail(),
                                utilisateur.getNom(),
                                utilisateur.getPrenom(),
                                utilisateur.getRole() != null ? utilisateur.getRole().getNom() : null,
                                utilisateur.getTelephone(),
                                utilisateur.getRole() != null ? utilisateur.getRole().getId() : null,
                                utilisateur.getRole() != null ? utilisateur.getRole().getCode() : null,
                                idEmploye,
                                idAgence
                ));
    }
}

