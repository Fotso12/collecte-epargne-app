package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.entities.Notification;
import com.collecte_epargne.collecte_epargne.mappers.NotificationMapper;
import com.collecte_epargne.collecte_epargne.services.implementations.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Endpoint pour envoyer une nouvelle notification
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationDto notificationDto) {
        logger.info("Requête reçue pour envoyer une notification au client : {}", notificationDto.getCodeClient());

        try {
            // 1. Conversion DTO -> Entity via MapStruct
            Notification notification = notificationMapper.toEntity(notificationDto);

            // 2. Sécurité : On s'assure que le codeClient est bien présent (cas où le mapper échouerait)
            if (notification.getCodeClient() == null && notificationDto.getCodeClient() != null) {
                logger.warn("Le codeClient était nul après mapping, correction manuelle appliquée.");
                notification.setCodeClient(notificationDto.getCodeClient());
            }

            // 3. Initialisation des champs obligatoires
            notification.setIdNotification(UUID.randomUUID().toString());
            notification.setStatut("CREE");
            notification.setDateCreation(Instant.now());

            // 4. Appel du service pour sauvegarde et envoi RabbitMQ
            notificationService.sendNotification(notification);

            logger.info("Notification {} créée avec succès pour le client {}",
                    notification.getIdNotification(), notification.getCodeClient());

            return new ResponseEntity<>(notificationMapper.toDto(notification), HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification : {}", e.getMessage(), e);
            return new ResponseEntity<>("Erreur interne : " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupérer l'historique complet
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAll() {
        logger.info("Récupération de toutes les notifications");
        try {
            List<NotificationDto> notifications = notificationService.getAllNotifications()
                    .stream()
                    .map(notificationMapper::toDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur getAll : {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur", e);
        }
    }

    /**
     * Récupérer une notification spécifique par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        logger.info("Recherche notification ID : {}", id);
        try {
            Notification notification = notificationService.getNotificationById(id);
            return new ResponseEntity<>(notificationMapper.toDto(notification), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Notification introuvable", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Récupérer toutes les notifications d'un client spécifique
     */
    @GetMapping("/client/{codeClient}")
    public ResponseEntity<List<NotificationDto>> getByClient(@PathVariable String codeClient) {
        logger.info("Récupération notifications pour client : {}", codeClient);
        try {
            List<NotificationDto> notifications = notificationService.getNotificationsByClient(codeClient)
                    .stream()
                    .map(notificationMapper::toDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la recherche", e);
        }
    }

    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        try {
            notificationService.markAsRead(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            notificationService.deleteNotification(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}