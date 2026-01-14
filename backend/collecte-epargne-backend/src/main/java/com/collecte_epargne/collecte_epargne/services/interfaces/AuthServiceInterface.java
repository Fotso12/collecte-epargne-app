package com.collecte_epargne.collecte_epargne.services.interfaces;

public interface AuthServiceInterface {


    // 1️⃣ Envoi du code par email
    void envoyerCode(String email);

    // 1️⃣ ENVOYER LE CODE PAR EMAIL
    void envoyerCodeReinitialisation(String email);

    // 2️⃣ VÉRIFIER LE CODE
    void verifierCode(String email, String code);

    // 3️⃣ RÉINITIALISER LE MOT DE PASSE
    void reinitialiserMotDePasse(String email, String code, String nouveauMotDePasse);

    void sendResetCode(String email);

    void verifyCode(String email, String code);

    void resetPassword(String email, String code, String newPassword);

    // 3️⃣ Réinitialisation du mot de passe
    void resetPassword(String email, String nouveauMotDePasse);
}
