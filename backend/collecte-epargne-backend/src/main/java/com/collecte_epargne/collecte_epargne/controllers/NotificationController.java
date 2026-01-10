package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.entities.Notification;
import com.collecte_epargne.collecte_epargne.mappers.NotificationMapper;
import com.collecte_epargne.collecte_epargne.repositories.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAll() {
        List<NotificationDto> dtos = notificationRepository.findAll().stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<NotificationDto> send(@RequestBody NotificationDto dto) {
        // Enforce creation date
        dto.setDateEnvoi(LocalDateTime.now());
        
        Notification entity = notificationMapper.toEntity(dto);
        Notification saved = notificationRepository.save(entity);
        return new ResponseEntity<>(notificationMapper.toDto(saved), HttpStatus.CREATED);
    }
}
