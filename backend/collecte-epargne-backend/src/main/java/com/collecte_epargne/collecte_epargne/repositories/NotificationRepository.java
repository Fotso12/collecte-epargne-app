package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    // Trouver une notification par ID
    Optional<Notification> findByIdNotification(String idNotification);

    // Trouver les notifications par code client
    List<Notification> findByCodeClient(String codeClient);

    // Trouver les notifications par statut
    List<Notification> findByStatut(String statut);
}
