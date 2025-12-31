package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.NotificationDto;
import com.collecte_epargne.collecte_epargne.entities.Notification;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "transaction", target = "idTransaction", qualifiedByName = "transactionToId")
    NotificationDto toDto(Notification notification);

    @Named("transactionToId")
    default String transactionToId(Transaction transaction) {
        return transaction != null ? transaction.getIdTransaction() : null;
    }

    // On s'assure que codeClient est bien copié du DTO vers l'entité
    @Mapping(target = "transaction", ignore = true)
    @Mapping(source = "codeClient", target = "codeClient")
    Notification toEntity(NotificationDto notificationDto);
}