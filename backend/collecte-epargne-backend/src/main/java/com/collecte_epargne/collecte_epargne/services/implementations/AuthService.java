package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.entities.PasswordResetCode;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.repositories.PasswordResetCodeRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.AuthServiceInterface;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService implements AuthServiceInterface {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       PasswordResetCodeRepository passwordResetCodeRepository,
                       EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordResetCodeRepository = passwordResetCodeRepository;
        this.emailService = emailService;
    }

    // Envoi du code par email
    @Override
    @Transactional
    public void envoyerCode(String email) {
        // Utilisation de findByEmailIgnoreCase pour plus de souplesse
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur avec cet email"));

        // Nettoyage des anciens codes pour éviter les doublons (Erreur 7 results)
        passwordResetCodeRepository.deleteByEmail(email.trim());

        String code = String.valueOf(100000 + new Random().nextInt(900000));

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setEmail(email.trim());
        resetCode.setCode(code);
        resetCode.setExpirationTime(LocalDateTime.now().plusMinutes(10));

        passwordResetCodeRepository.save(resetCode);

        // Envoi par email
        emailService.sendVerificationCode(email.trim(), code);
        
        System.out.println("--- LOG DEBUG --- CODE GÉNÉRÉ POUR " + email + " : " + code);
    }

    // Vérification du code
    @Override
    public void verifierCode(String email, String code) {
        PasswordResetCode resetCode = passwordResetCodeRepository
                .findByEmailAndCode(email.trim(), code)
                .orElseThrow(() -> new RuntimeException("Code invalide"));

        if (resetCode.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Code expiré");
        }
    }

    // Réinitialisation du mot de passe
    @Override
    @Transactional
    public void resetPassword(String email, String nouveauMotDePasse) {
        // 1. Nettoyage de l'email reçu (important pour la comparaison)
        String emailNettoye = email.trim();

        // 2. Diagnostic Console
        System.out.println("--- VÉRIFICATION DE LA BASE DE DONNÉES ---");
        System.out.println("Recherche pour l'email : [" + emailNettoye + "]");
        utilisateurRepository.findAll().forEach(u ->
                System.out.println("Utilisateur en BD -> Login: [" + u.getLogin() + "] | Email: [" + u.getEmail() + "]")
        );

        // 3. Recherche de l'utilisateur
        // On utilise findByEmailIgnoreCase car l'email n'est pas l'ID (@Id est le login)
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(emailNettoye)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 4. Mise à jour du mot de passe
        // Spring Security BCrypt s'occupe du hachage
        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));

        // 5. Sauvegarde (JPA fera un UPDATE car l'objet possède déjà son LOGIN/ID)
        utilisateurRepository.save(utilisateur);

        // 6. Nettoyage final du code utilisé
        passwordResetCodeRepository.deleteByEmail(emailNettoye);

        System.out.println("SUCCÈS : Mot de passe mis à jour pour le login [" + utilisateur.getLogin() + "]");
    }

    // --- Méthodes de l'interface conservées pour éviter les erreurs de compilation ---
    @Override public void envoyerCodeReinitialisation(String email) {}
    @Override public void reinitialiserMotDePasse(String email, String code, String nouveauMotDePasse) {}
    @Override public void sendResetCode(String email) {}
    @Override public void verifyCode(String email, String code) {}
    @Override public void resetPassword(String email, String code, String newPassword) {}
}