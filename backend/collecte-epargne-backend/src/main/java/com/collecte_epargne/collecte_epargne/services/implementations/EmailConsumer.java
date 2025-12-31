package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ClientRepository clientRepository;

    @RabbitListener(queues = "email.notifications")
    public void processEmailNotification(NotificationDto notificationDto) {
        logger.info("Consumer Email: Traitement de la notification {}", notificationDto.getIdNotification());
        try {
            String recipientEmail = getRecipientEmail(notificationDto);

            if (recipientEmail != null && !recipientEmail.isEmpty()) {
                logger.info("Envoi de l'email à : {}", recipientEmail);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(recipientEmail);
                message.setSubject(notificationDto.getTitre());
                message.setText(notificationDto.getMessage());

                mailSender.send(message);

                notificationService.updateNotificationStatus(
                        notificationDto.getIdNotification(),
                        "ENVOYE",
                        null
                );
            } else {
                logger.warn("Aucun email trouvé pour le client {}", notificationDto.getCodeClient());
                notificationService.updateNotificationStatus(
                        notificationDto.getIdNotification(),
                        "ERREUR",
                        "Adresse email non trouvée"
                );
            }
        } catch (Exception e) {
            logger.error("Échec de l'envoi SMTP: {}", e.getMessage());
            notificationService.updateNotificationStatus(
                    notificationDto.getIdNotification(),
                    "ERREUR",
                    e.getMessage()
            );
        }
    }

    private String getRecipientEmail(NotificationDto notificationDto) {
        if (notificationDto.getCodeClient() != null) {
            // On utilise la nouvelle méthode avec JOIN FETCH
            return clientRepository.findByCodeClientWithUtilisateur(notificationDto.getCodeClient())
                    .map(client -> {
                        // Ici, l'utilisateur est déjà chargé, plus d'erreur de session !
                        return client.getUtilisateur().getEmail();
                    })
                    .orElse(null);
        }
        return "tamofotso90@gmail.com";
    }
}