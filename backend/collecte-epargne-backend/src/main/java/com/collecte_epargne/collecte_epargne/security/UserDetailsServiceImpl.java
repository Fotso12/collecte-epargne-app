package com.collecte_epargne.collecte_epargne.security;

import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        // Gestion sécurisée du rôle (évite NullPointerException)
        // Note: Assurez-vous que le nom du rôle en BDD correspond exactement (ex: SUPERVISEUR)
        String roleName = (utilisateur.getRole() != null && utilisateur.getRole().getNom() != null)
                ? utilisateur.getRole().getNom()
                : "USER";

        // Utilisation de authorities au lieu de roles
        // Cela permet d'utiliser @PreAuthorize("hasAuthority('SUPERVISEUR')") sans le préfixe ROLE_
        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities(roleName)
                .build();
    }
}