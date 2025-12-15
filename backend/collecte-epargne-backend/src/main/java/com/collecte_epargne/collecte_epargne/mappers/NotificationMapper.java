package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.entities.Notification;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "transaction", target = "idTransaction")
    NotificationDto toDto(Notification notification);

    // --- Conversion Helper (Entité -> ID)
    default String transactionToId(Transaction transaction) {
        return transaction != null ? transaction.getIdTransaction() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels)
    @Mapping(target = "transaction", ignore = true)
    Notification toEntity(NotificationDto notificationDto);
}