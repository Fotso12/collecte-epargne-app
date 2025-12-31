package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.entities.Notification;
import com.collecte_epargne.collecte_epargne.mappers.NotificationMapper;
import com.collecte_epargne.collecte_epargne.repositories.NotificationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional // Ajouté ici : Garantit que toutes les écritures sont validées en base
public class NotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * Envoie une notification selon son type
     */
    public void sendNotification(Notification notification) {
        // 1. Sauvegarder l'entité en base
        Notification savedNotification = notificationRepository.save(notification);

        // 2. Convertir en DTO pour l'envoi
        NotificationDto dtoToSend = notificationMapper.toDto(savedNotification);

        // 3. Router vers RabbitMQ
        switch (notification.getType()) {
            case EMAIL -> rabbitTemplate.convertAndSend("email.notifications", dtoToSend);
            case PUSH -> rabbitTemplate.convertAndSend("push.notifications", dtoToSend);
            case IN_APP -> rabbitTemplate.convertAndSend("realtime.notifications", dtoToSend);
            case SMS -> rabbitTemplate.convertAndSend("sms.notifications", dtoToSend);
        }
    }

    /**
     * Met à jour le statut après l'action d'un Consumer
     */
    public void updateNotificationStatus(String idNotification, String statut, String erreurEnvoi) {
        notificationRepository.findById(idNotification).ifPresent(notification -> {
            notification.setStatut(statut);
            notification.setDateEnvoi(Instant.now());
            if (erreurEnvoi != null) {
                notification.setErreurEnvoi(erreurEnvoi);
            }
            notificationRepository.save(notification);
        });
    }

    /**
     * Marque une notification comme lue (CORRIGÉ)
     */
    public void markAsRead(String idNotification) {
        notificationRepository.findById(idNotification).ifPresent(notification -> {
            notification.setDateLecture(Instant.now());
            notification.setStatut("LU"); // On change aussi le statut pour plus de clarté
            notificationRepository.save(notification);

            // Log de confirmation pour le debug
            System.out.println("Notification " + idNotification + " mise à jour : STATUT=LU");
        });
    }

    @Transactional(readOnly = true) // Optimisation pour les lectures
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByClient(String codeClient) {
        return notificationRepository.findByCodeClient(codeClient);
    }

    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification non trouvée avec l'ID: " + id);
        }
        notificationRepository.deleteById(id);
    }
}