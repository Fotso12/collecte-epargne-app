package com.collecte_epargne.collecte_epargne.controllers;
import com.collecte_epargne.collecte_epargne.dtos.LoginRequest;
import com.collecte_epargne.collecte_epargne.dtos.LoginResponse;
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

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
                        request.email(),
                        request.password()
                )
        );

        // 2️⃣ Génération du token
        String token = jwtService.generateToken(request.email());

        // 3️⃣ Retour du token
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

