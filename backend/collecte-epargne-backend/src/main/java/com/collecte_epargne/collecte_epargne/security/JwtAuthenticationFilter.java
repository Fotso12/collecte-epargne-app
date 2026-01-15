package com.collecte_epargne.collecte_epargne.security;

import com.collecte_epargne.collecte_epargne.services.implementations.UtilisateurDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UtilisateurDetailsService utilisateurDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UtilisateurDetailsService utilisateurDetailsService
    ) {
        this.jwtService = jwtService;
        this.utilisateurDetailsService = utilisateurDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. NE PAS FILTRER les requêtes d'authentification ou les pré-vérifications CORS (OPTIONS)
        if (request.getServletPath().contains("/api/auth") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // 2. Pas de token ou format invalide
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("⛔ Header Authorization manquant ou invalide pour : {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraction et validation du token
        String jwt = authHeader.substring(7);
        String email;

        try {
            email = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            log.error("Erreur d'extraction du token : {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token JWT invalide ou expiré\"}");
            return;
        }

        // 4. Si l'utilisateur n'est pas encore authentifié dans le contexte actuel
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // LOG DE DIAGNOSTIC POUR LA 403
                log.info("JWT Valide. Utilisateur: {} | Rôles détectés: {}", email, userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Mettre l'utilisateur dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("Token invalide pour l'utilisateur : {}", email);
            }
        }

        // Continuer la chaîne vers le contrôleur
        filterChain.doFilter(request, response);
    }
}