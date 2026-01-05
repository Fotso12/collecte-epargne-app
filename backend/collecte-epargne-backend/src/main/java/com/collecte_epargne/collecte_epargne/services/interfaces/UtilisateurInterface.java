package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;

import java.util.List;

public interface UtilisateurInterface {
    /**
     * Sauvegarde un nouvel utilisateur. Le DTO n'inclut pas le mot de passe,
     * mais l'entité doit être hydratée avec le mot de passe HACHÉ.
     */
    UtilisateurDto save(UtilisateurDto utilisateurDto, String password);

    List<UtilisateurDto> getAll();

    UtilisateurDto getByLogin(String login);

    /**
     * Met à jour les informations de l'utilisateur (hors mot de passe).
     */
    UtilisateurDto update(String login, UtilisateurDto utilisateurDto);

    /**
     * Met à jour uniquement le mot de passe d'un utilisateur.
     */
    void updatePassword(String login, String newPassword);

    void delete(String login);

    // Fonction spécifique
    UtilisateurDto getByEmail(String email);


    UtilisateurDto updateStatut(String login, String statut);


}
