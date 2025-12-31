package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.entities.DeviceToken;
import com.collecte_epargne.collecte_epargne.repositories.DeviceTokenRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PushConsumer {

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${onesignal.app-id}")
    private String oneSignalAppId;

    @Value("${onesignal.rest-api-key}")
    private String oneSignalRestApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @RabbitListener(queues = "push.notifications")
    public void processPushNotification(NotificationDto notificationDto) {
        try {
            List<DeviceToken> deviceTokens = getDeviceTokens(notificationDto);

            if (!deviceTokens.isEmpty()) {
                sendToOneSignal(notificationDto, deviceTokens);

                notificationService.updateNotificationStatus(
                        notificationDto.getIdNotification(),
                        "ENVOYE",
                        null
                );
            } else {
                notificationService.updateNotificationStatus(
                        notificationDto.getIdNotification(),
                        "ERREUR",
                        "Aucun device token trouvé"
                );
            }
        } catch (Exception e) {
            notificationService.updateNotificationStatus(
                    notificationDto.getIdNotification(),
                    "ERREUR",
                    e.getMessage()
            );
        }
    }

    private List<DeviceToken> getDeviceTokens(NotificationDto notificationDto) {
        if (notificationDto.getCodeClient() != null) {
            return deviceTokenRepository.findByUtilisateurLoginAndActif(
                    notificationDto.getCodeClient(), true);
        }
        return deviceTokenRepository.findByActif(true);
    }

    private void sendToOneSignal(NotificationDto notificationDto, List<DeviceToken> deviceTokens) {
        String url = "https://onesignal.com/api/v1/notifications";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + oneSignalRestApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("app_id", oneSignalAppId);
        body.put("headings", Map.of("en", notificationDto.getTitre()));
        body.put("contents", Map.of("en", notificationDto.getMessage()));

        List<String> playerIds = deviceTokens.stream()
                .map(DeviceToken::getToken)
                .toList();
        body.put("include_player_ids", playerIds);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erreur OneSignal: " + response.getBody());
        }
    }
}