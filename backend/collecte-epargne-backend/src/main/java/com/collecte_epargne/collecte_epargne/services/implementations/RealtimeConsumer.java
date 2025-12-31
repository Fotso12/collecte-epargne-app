package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RealtimeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeConsumer.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = "realtime.notifications")
    public void processRealtimeNotification(NotificationDto notificationDto) {
        try {
            logger.info("Processing realtime notification: {}", notificationDto.getIdNotification());

            // Envoyer le DTO via WebSocket
            messagingTemplate.convertAndSend("/topic/notifications", notificationDto);

            if (notificationDto.getCodeClient() != null) {
                messagingTemplate.convertAndSendToUser(
                        notificationDto.getCodeClient(),
                        "/queue/notifications",
                        notificationDto
                );
            }

            notificationService.updateNotificationStatus(
                    notificationDto.getIdNotification(),
                    "ENVOYE",
                    null
            );

        } catch (Exception e) {
            logger.error("Error processing notification {}: {}", notificationDto.getIdNotification(), e.getMessage());
            notificationService.updateNotificationStatus(
                    notificationDto.getIdNotification(),
                    "ERREUR",
                    e.getMessage()
            );
        }
    }
}