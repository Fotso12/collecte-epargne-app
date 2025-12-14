package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {

    // Trouver un utilisateur par son email (souvent utilisé pour la connexion)
    Optional<Utilisateur> findByEmail(String email);

    // Trouver tous les utilisateurs ayant un rôle spécifique
    List<Utilisateur> findByRoleId(Integer idRole);
}