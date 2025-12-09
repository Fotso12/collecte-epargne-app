package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RoleMapper {


    RoleDto toDto(Role role);

    Role toEntity(RoleDto roleDto);
}