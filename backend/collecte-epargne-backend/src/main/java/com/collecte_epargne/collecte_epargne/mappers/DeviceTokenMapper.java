package com.collecte_epargne.collecte_epargne.mappers;


import com.collecte_epargne.collecte_epargne.dtos.DeviceTokenDto;
import com.collecte_epargne.collecte_epargne.entities.DeviceToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {

    DeviceTokenDto toDto(DeviceToken deviceToken);

    @Mapping(target = "utilisateur", ignore = true)
    DeviceToken toEntity(DeviceTokenDto deviceTokenDto);
}
