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
        String roleName = (utilisateur.getRole() != null && utilisateur.getRole().getNom() != null)
                ? utilisateur.getRole().getNom()
                : "USER"; // Rôle par défaut si null

        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .roles(roleName)
                .build();
    }
}
