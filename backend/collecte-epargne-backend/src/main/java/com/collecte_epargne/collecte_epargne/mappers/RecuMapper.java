package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.RecuDto;
import com.collecte_epargne.collecte_epargne.entities.Recu;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RecuMapper {

    @Mapping(source = "transaction.idTransaction", target = "idTransaction")
    RecuDto toDto(Recu recu);

    @Mapping(source = "idTransaction", target = "transaction.idTransaction")
    Recu toEntity(RecuDto recuDto);
}