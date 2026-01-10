package com.collecte_epargne.collecte_epargne.services.implementations;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsable de l'envoi d'emails dans l'application.
 * Utilise JavaMailSender pour envoyer des emails via SMTP (configuré dans application.properties).
 */
@Service
public class EmailService {

    // Injection du JavaMailSender pour l'envoi d'emails
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un email avec les identifiants de connexion à un nouvel utilisateur.
     * L'email contient le login et le mot de passe en clair.
     *
     * @param toEmail Adresse email du destinataire (l'utilisateur)
     * @param login   Login de l'utilisateur
     * @param password Mot de passe en clair de l'utilisateur
     */
    public void sendUserCredentialsEmail(String toEmail, String login, String password) {
        // Création d'un message email simple
        SimpleMailMessage message = new SimpleMailMessage();

        // Configuration du destinataire
        message.setTo(toEmail);

        // Sujet de l'email
        message.setSubject("Vos identifiants de connexion");

        // Corps de l'email avec les identifiants
        message.setText("Bonjour,\n\nVos identifiants de connexion sont :\n\nLogin : " + login + "\nMot de passe : " + password + "\n\nVeuillez changer votre mot de passe après la première connexion.\n\nCordialement,\nL'équipe Collecte Épargne");

        // Envoi de l'email via JavaMailSender
        mailSender.send(message);
    }
}
