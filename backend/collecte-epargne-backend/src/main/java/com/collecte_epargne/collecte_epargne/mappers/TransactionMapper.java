package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "compte", target = "idCompte")
    @Mapping(source = "initiateur", target = "idEmployeInitiateur")
    @Mapping(source = "caissierValidateur", target = "idCaissierValidateur")
    @Mapping(source = "superviseurValidateur", target = "idSuperviseurValidateur")
    TransactionDto toDto(Transaction transaction);

    // --- Conversion Helper (Entité -> ID)
    default String compteToId(Compte compte) {
        return compte != null ? compte.getIdCompte() : null;
    }

    default String employeToId(Employe employe) {
        return employe != null ? employe.getIdEmploye() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels et collections)
    @Mapping(target = "compte", ignore = true)
    @Mapping(target = "initiateur", ignore = true)
    @Mapping(target = "caissierValidateur", ignore = true)
    @Mapping(target = "superviseurValidateur", ignore = true)
    @Mapping(target = "recu", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Transaction toEntity(TransactionDto transactionDto);
}