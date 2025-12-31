package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    // Trouver les tokens actifs par utilisateur
    List<DeviceToken> findByUtilisateurLoginAndActif(String utilisateurLogin, Boolean actif);

    // Trouver un token spécifique
    Optional<DeviceToken> findByToken(String token);

    // Trouver tous les tokens actifs
    List<DeviceToken> findByActif(Boolean actif);

    // Trouver les tokens par type de device
    List<DeviceToken> findByDeviceTypeAndActif(String deviceType, Boolean actif);
}
