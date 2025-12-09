package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.RecuDto;
import com.collecte_epargne.collecte_epargne.entities.Recu;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RecuMapper {

    @Mapping(source = "transaction", target = "idTransaction")
    RecuDto toDto(Recu recu);

    // --- Conversion Helper (Entité -> ID)
    default String transactionToId(Transaction transaction) {
        return transaction != null ? transaction.getIdTransaction() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer l'objet relationnel)
    @Mapping(target = "transaction", ignore = true)
    Recu toEntity(RecuDto recuDto);
}