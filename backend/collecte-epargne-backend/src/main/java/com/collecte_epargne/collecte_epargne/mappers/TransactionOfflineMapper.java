package com.collecte_epargne.collecte_epargne.mappers;

import java.time.Instant;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.collecte_epargne.collecte_epargne.entities.TransactionOffline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TransactionOfflineMapper {

    @Mapping(source = "employe", target = "idEmploye")
    @Mapping(source = "client", target = "codeClient")
    @Mapping(source = "compte", target = "idCompte")
    @Mapping(source = "transactionFinale", target = "idTransactionFinale")
    @Mapping(target = "dateTransaction", ignore = true)
    TransactionOfflineDto toDto(TransactionOffline transactionOffline);

    // --- Conversion Helper (Entité -> ID)
    default Integer employeToId(Employe employe) {
        return employe != null ? employe.getIdEmploye() : null;
    }

    default Long clientToCode(Client client) {
        return client != null ? client.getNumeroClient() : null;
    }

    default String compteToId(Compte compte) {
        return compte != null ? compte.getIdCompte() : null;
    }

    default String transactionToId(Transaction transaction) {
        return transaction != null ? transaction.getIdTransaction() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels)
    @Mapping(target = "employe", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "compte", ignore = true)
    @Mapping(target = "transactionFinale", ignore = true)
    @Mapping(target = "dateTransaction", ignore = true)
    TransactionOffline toEntity(TransactionOfflineDto transactionOfflineDto);
}